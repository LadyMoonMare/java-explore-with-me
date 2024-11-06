package ru.yandex.practicum.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    @Transactional
    UserDto addUser(NewUserRequest request);

    @Transactional
    void deleteUser(Long userId);

    List<UserDto> getUsers(Integer[] ids, Integer from, Integer size);
}
