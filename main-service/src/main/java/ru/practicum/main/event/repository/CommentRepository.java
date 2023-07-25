package ru.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.main.event.model.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);
}