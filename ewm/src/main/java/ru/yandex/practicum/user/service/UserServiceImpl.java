package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest request) {
        try {
            log.info("adding user to repo: email {}, name {}", request.getEmail(),request.getName());
            User newUser = userRepository.save(UserMapper.fromUserRequestToUser(request));

            log.info("adding success");
            return UserMapper.toUserDtoFromUser(newUser);
        } catch (DataIntegrityViolationException e) {
            log.warn("adding user failure");
            throw new ConflictException("This email" + request.getEmail()
                    + "is already in use by another user");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("searching for user id = {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("deleting failure");
            return new NotFoundException("User id =" + userId + "is not found");
        });

        userRepository.delete(user);
        log.info("deleting success");
    }

    @Override
    public List<UserDto> getUsers(Integer[] ids, Integer from, Integer size) {
        if (ids == null) {
            if (from == 0) {
                log.info("getting all users only with limit");
                return userRepository.findAllButSize(size).stream()
                        .map(UserMapper::toUserDtoFromUser)
                        .collect(Collectors.toList());
            } else {
                log.info("getting all users with limit and from");
                return userRepository.findAllButLimit(from,size).stream()
                        .map(UserMapper::toUserDtoFromUser)
                        .collect(Collectors.toList());
            }
        } else {
            log.info("getting all users");
            List<User> allUsers = userRepository.findAll();
            List<User> finalList = new ArrayList<>();

            log.info("sorting users by ids");
            for (Integer id : ids) {
                for (User user : allUsers) {
                    if (Objects.equals(user.getId(), Long.valueOf(id))) {
                        finalList.add(user);
                        log.info("added id = {}", id);
                    }
                }
            }

            if (finalList.isEmpty()) {
                log.info("no such ids, return empty list");
                return new ArrayList<>();
            } else {
                log.info("returning list of users with special ids");
                return finalList.stream()
                        .map(UserMapper::toUserDtoFromUser)
                        .collect(Collectors.toList());
            }
        }
    }
}
