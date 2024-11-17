package ru.yandex.practicum.user.service;

import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(NewUserRequest request);

    void deleteUser(Long userId);

    List<UserDto> getUsers(Integer[] ids, Integer from, Integer size);
}
