package ru.practicum.main.user.service;

import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUserAsAdmin(NewUserRequest newUserRequest);

    List<UserDto> getAllUsersAsAdmin(List<Long> ids, int from, int size);

    void deleteUserAsAdminById(Long userId);

    User getUserById(Long userId);

}
