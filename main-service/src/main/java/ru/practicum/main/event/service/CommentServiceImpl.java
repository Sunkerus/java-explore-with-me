package ru.practicum.main.event.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.dto.CommentDto;
import ru.practicum.main.event.dto.CommentFullDto;
import ru.practicum.main.event.dto.CommentIncomeDto;
import ru.practicum.main.event.dto.mappers.CommentMapper;
import ru.practicum.main.event.enums.CommentState;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.enums.RequestStatus;
import ru.practicum.main.event.model.Comment;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.QComment;
import ru.practicum.main.event.model.Request;
import ru.practicum.main.event.repository.CommentRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.event.repository.RequestRepository;
import ru.practicum.main.exeption.BadRequest;
import ru.practicum.main.exeption.ConflictException;
import ru.practicum.main.exeption.NotFoundException;
import ru.practicum.main.helper.QPredicates;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentDto postCommentAsPrivate(CommentIncomeDto commentIncomeDto, Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Cannot found user with id: %d", userId));
        });
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    throw new NotFoundException(String.format("Comment with this id: %d cannot found", eventId));
                });

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new BadRequest("Comment cannot be write to unpublished event");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentIncomeDto, user, event)));
    }

    @Override
    @Transactional
    public CommentDto patchCommentAsPrivate(CommentIncomeDto commentIncomeDto, Long userId, Long eventId, Long commentId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id: %d was not found", userId));
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Comment with this id: %d cannot found", eventId));
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Comment with this id: %d cannot found", eventId));
        });

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ConflictException(String.format(
                    "The comment with id: %d doesn't belong to event wit id=%d", comment.getEvent().getId(), eventId));
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException(String.format(
                    "The comment with id: %d doesn't belong to event wit id=%d", comment.getAuthor().getId(), eventId));
        }

        comment.setState(CommentState.EDITED);
        comment.setText(commentIncomeDto.getText());

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentAsPrivate(Long userId, Long eventId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id:%d was not found", userId));
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Comment with this id: %d cannot found", eventId));
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Comment with this id: %dcommentId cannot found", commentId));
        });
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ConflictException(
                    String.format("The user with id=%d cannot %s comment as he/she didn't leave it.", userId, comment.getText()));
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto getCommentAsPrivate(Long authorId, Long commentId) {

        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException(String.format("User with id:%d was not found", authorId));
        }

        Comment comment = commentRepository.findByIdAndAuthorId(commentId, authorId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "User with id: %d doesn't have comment with id %d", authorId, commentId)));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAllOwnerCommentsAsPrivate(
            Long authorId,
            Long eventId,
            String text,
            LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Pageable page) {

        checkRangeTime(rangeStart, rangeEnd);

        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException(String.format("User with id: %d cannot found", authorId));
        }

        Predicate pred = createPredicateOfComment(authorId, eventId, text, rangeStart, rangeEnd);

        return commentRepository.findAll(pred, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsAsPublic(
            Long eventId,
            String text,
            String authorName,
            LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Pageable page) {

        checkRangeTime(rangeStart, rangeEnd);
        Predicate pred = createPublicPredicateOfComment(eventId, text, authorName, rangeStart, rangeEnd);

        if (pred != null) {
            return commentRepository.findAll(pred, page)
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }

        return commentRepository.findAll(page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentByIdAsPublic(Long commentId) {
        return CommentMapper.toCommentDto(commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(
                String.format("Cannot find comment by id: %d", commentId))));
    }

    @Override
    @Transactional
    public CommentFullDto patchCommentAsAdmin(CommentIncomeDto commentIncomeDto, Long commentId, Long eventId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format(
                "Cannot find comment by id: %d", commentId)));

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Comment with this id: %d cannot found", eventId));
        }

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ConflictException(String.format(
                    "The comment with id: %d doesn't belong to event wit id: %d", comment.getEvent().getId(), eventId));
        }

        comment.setText(commentIncomeDto.getText());
        comment.setState(CommentState.CHANGING_BY_ADMIN);

        return CommentMapper.toCommentFullDto(comment, requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).intValue());
    }

    @Override
    public CommentFullDto getCommentAsAdmin(Long commentId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Comment with this id: %d cannot found", commentId));
        });

        return CommentMapper
                .toCommentFullDto(comment, requestRepository.countByEventIdAndStatus(comment.getEvent().getId(), RequestStatus.CONFIRMED).intValue());
    }

    @Override
    public List<CommentFullDto> getAllCommentsAsAdmin(
            Long eventId,
            Long authorId,
            String text,
            LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Pageable page) {

        checkRangeTime(rangeStart, rangeEnd);
        Predicate pred = createPredicateOfComment(authorId, eventId, text, rangeStart, rangeEnd);


        List<Comment> comments;

        Map<Event, Long> mapStorage;

        if (pred != null) {
            comments = commentRepository.findAll(pred, page).toList();

            mapStorage = requestRepository.findByStatusAndEvent_IdIn(RequestStatus.CONFIRMED, comments
                            .stream()
                            .map(Comment::getId)
                            .collect(Collectors.toList())).stream()
                    .collect(Collectors.groupingBy(Request::getEvent, Collectors.counting()));

            return comments
                    .stream()
                    .map(comment ->
                            CommentMapper.toCommentFullDto(comment,
                                    mapStorage.getOrDefault(comment.getEvent(), 0L).intValue()))
                    .collect(Collectors.toList());
        } else {

            comments = commentRepository.findAll(page).toList();

            mapStorage = requestRepository.findByStatusAndEvent_IdIn(RequestStatus.CONFIRMED, comments
                            .stream()
                            .map(Comment::getId)
                            .collect(Collectors.toList())).stream()
                    .collect(Collectors.groupingBy(Request::getEvent, Collectors.counting()));
        }

        return comments
                .stream()
                .map(comment ->
                        CommentMapper.toCommentFullDto(comment, mapStorage.getOrDefault(comment.getEvent(), 0L).intValue()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommentAsAdmin(Long commentId) {

        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(String.format("Comment with id: %d cannot found and delete", commentId));
        }

        commentRepository.deleteById(commentId);
    }


    private Predicate createPredicateOfComment(
            Long authorId,
            Long eventId,
            String text,
            LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        QComment comment = QComment.comment;

        return QPredicates.build()
                .add(comment.author.id.eq(authorId))
                .add(eventId != null ? comment.event.id.eq(eventId) : null)
                .add(!text.isEmpty() ? comment.text.containsIgnoreCase(text) : null)
                .add(rangeStart != null ? comment.created.goe(rangeStart) : null)
                .add(rangeEnd != null ? comment.created.loe(rangeEnd) : null)
                .buildAnd();
    }

    private void checkRangeTime(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ConflictException(String.format(
                    "Incorrect start: %s and end date: %s or end is before than after", startDate, endDate));
        }
    }


    private Predicate createPublicPredicateOfComment(
            Long eventId,
            String text,
            String authorName,
            LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        QComment comment = QComment.comment;

        return QPredicates.build()
                .add(eventId != null ? comment.id.eq(eventId) : null)
                .add(!text.isEmpty() ? comment.text.containsIgnoreCase(text) : null)
                .add(!authorName.isEmpty() ? comment.author.name.containsIgnoreCase(authorName) : null)
                .add(rangeStart != null ? comment.created.goe(rangeStart) : null)
                .add(rangeEnd != null ? comment.created.loe(rangeEnd) : null)
                .buildAnd();
    }


}