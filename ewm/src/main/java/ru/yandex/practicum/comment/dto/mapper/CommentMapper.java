package ru.yandex.practicum.comment.dto.mapper;

import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.ShortCommentDto;
import ru.yandex.practicum.comment.model.Comment;

public class CommentMapper {
    public static CommentDto fromCommentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .description(comment.getDescription())
                .state(comment.getState())
                .created(comment.getCreated())
                .published(comment.getPublished())
                .event(comment.getEvent())
                .build();
    }

    public static ShortCommentDto fromCommentToShortDto(Comment comment) {
        return ShortCommentDto.builder()
                .id(comment.getId())
                .author(comment.getAuthor().getName())
                .description(comment.getDescription())
                .event(comment.getEvent().getId())
                .published(comment.getPublished())
                .build();
    }
}
