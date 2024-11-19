package ru.yandex.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.UpdateAdminDto;
import ru.yandex.practicum.comment.service.CommentServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {
    private final CommentServiceImpl commentService;

    @GetMapping("/{comId}")
    public CommentDto getComment(@PathVariable @Positive Long comId) {
        log.info("attempt to get comment {} by admin",comId);
        return commentService.getCommentByAdmin(comId);
    }

    @PatchMapping("/{comId}")
    public CommentDto moderateComment(@PathVariable @Positive Long comId,
                                      @RequestBody @Valid UpdateAdminDto dto) {
        log.info("moderating comment {} by admin",comId);
        return commentService.approveCommentByAdmin(comId, dto);
    }

    @GetMapping
    public List<CommentDto> getComments(@RequestParam(required = false) Long[] events,
                                        @RequestParam(required = false) String[] states,
                                        @RequestParam(required = false)  String rangeStart,
                                        @RequestParam(required = false)  String rangeEnd,
                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = toLocalDateTime(rangeStart);
        }

        if (rangeEnd != null) {
            end = toLocalDateTime(rangeEnd);
        }
        log.info("attempt to get all events by admin with params: users =  {}, states = {}," +
                        " start = {}, end = {}, from = {}, size = {}", Arrays.toString(events),
                Arrays.toString(states), start, end, from, size);
        return commentService.getCommentsByAdmin(events,states,start,end,from,size);
    }

    private LocalDateTime toLocalDateTime(String s) {
        log.info("attempt to parse {} to localDateTime", s);
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
