package ru.yandex.practicum.comment.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.dto.ShortCommentDto;
import ru.yandex.practicum.comment.dto.UpdateAdminDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto dto);

    CommentDto updateCommentByUser(Long userId, Long eventId, Long commentId, NewCommentDto dto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    CommentDto getCommentById(Long userId, Long eventId, Long commentId);

    CommentDto getCommentByAdmin(Long commentId);

    CommentDto approveCommentByAdmin(Long commentId, UpdateAdminDto dto);

    List<CommentDto> getCommentsByAdmin(Long[] events, String[] states,
                                        LocalDateTime start, LocalDateTime end, Integer from,
                                        Integer size);

    List<ShortCommentDto> getCommentsToEventByPublic(Long eventId, String sort, Integer from,
                                                     Integer size, HttpServletRequest request);
}
