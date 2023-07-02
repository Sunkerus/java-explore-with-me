package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exeption.NotFoundException;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.helper.FurtherPageRequest;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUserViaAdmin(NewUserRequest newUserRequest) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserRequest)));
    }

    @Override
    public List<UserDto> getAllUsersViaAdmin(List<Long> ids, int from, int size) {
        if (ids == null || ids.isEmpty()) {
            return UserMapper.toUserDto(userRepository.findAll(new FurtherPageRequest(from, size)));
        }

        return UserMapper.toUserDto(userRepository.findAllByIdIn(
                ids, new FurtherPageRequest(from, size, Sort.by("id").descending())));
    }

    @Override
    @Transactional
    public void deleteUserViaAdminById(Long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
    }
}
