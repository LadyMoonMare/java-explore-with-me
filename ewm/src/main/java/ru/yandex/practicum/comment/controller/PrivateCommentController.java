package ru.yandex.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.service.CommentServiceImpl;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class PrivateCommentController {
    private final CommentServiceImpl commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                                 @RequestBody @Valid NewCommentDto dto) {
        log.info("attempt to add new comment to event {} by user {}", eventId, userId);
        return commentService.addComment(userId, eventId, dto);
    }

    @PatchMapping("/{comId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                                    @PathVariable @Positive Long comId,
                                    @RequestBody @Valid NewCommentDto dto) {
        log.info("attempt to update comment {} by user {}", comId, userId);
        return commentService.updateCommentByUser(userId, eventId, comId, dto);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                              @PathVariable @Positive Long comId) {
        log.info("attempt to delete comment {} by user {}", comId, userId);
        commentService.deleteComment(userId, eventId, comId);
    }

    @GetMapping("/{comId}")
    public CommentDto getComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                                 @PathVariable @Positive Long comId) {
        log.info("attempt to get comment {} by user {}", comId, userId);
        return commentService.getCommentById(userId, eventId, comId);
    }
}
