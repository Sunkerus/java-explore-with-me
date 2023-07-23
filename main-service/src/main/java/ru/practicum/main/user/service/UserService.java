package ru.practicum.main.user.service;

import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUserViaAdmin(NewUserRequest newUserRequest);

    List<UserDto> getAllUsersViaAdmin(List<Long> ids, int from, int size);

    void deleteUserViaAdminById(Long userId);

    User getUserById(Long userId);

}
