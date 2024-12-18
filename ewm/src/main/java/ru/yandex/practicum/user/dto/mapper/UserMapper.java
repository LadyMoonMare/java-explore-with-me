package ru.yandex.practicum.user.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserShortDto;
import ru.yandex.practicum.user.model.User;

@UtilityClass
public class UserMapper {
    public static User fromUserRequestToUser(NewUserRequest request) {
        return new User(request.getEmail(), request.getName());
    }

    public static UserDto toUserDtoFromUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserShortDto fromUserToShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
