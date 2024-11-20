package ru.yandex.practicum.comment.dto.mapper;

import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.ShortCommentDto;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.event.dto.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.dto.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;

public class CommentMapper {
    public static CommentDto fromCommentToDto(Comment comment, User user, Event event) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(UserMapper.fromUserToShortDto(user))
                .description(comment.getDescription())
                .state(comment.getState())
                .created(comment.getCreated())
                .published(comment.getPublished())
                .event(EventMapper.fromEventToShortDto(event))
                .build();
    }

    public static ShortCommentDto fromCommentToShortDto(Comment comment, User user) {
        return ShortCommentDto.builder()
                .id(comment.getId())
                .author(user.getName())
                .description(comment.getDescription())
                .event(comment.getEvent())
                .published(comment.getPublished())
                .build();
    }
}
