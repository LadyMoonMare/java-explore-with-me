package ru.yandex.practicum.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.dto.ShortCommentDto;
import ru.yandex.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<ShortCommentDto> getComments(@PathVariable @Positive Long eventId,
                                             @RequestParam(defaultValue = "NEW") @NotBlank String sort,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size,
                                             HttpServletRequest request) {
        log.info("attempt to get comments to event {} by public", eventId);
        return commentService.getCommentsToEventByPublic(eventId, sort, from, size, request);
    }
}
