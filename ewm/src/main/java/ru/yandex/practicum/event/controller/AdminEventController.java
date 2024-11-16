package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.update.UpdateEventAdminRequest;
import ru.yandex.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventDto> getEvents(@RequestParam(required = false) Long[] users,
                                    @RequestParam(required = false) String[] states,
                                    @RequestParam(required = false) Long[] categories,
                                    @RequestParam(required = false)  String rangeStart,
                                    @RequestParam(required = false)  String rangeEnd,
                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("from {}, size {}", from, size);
        LocalDateTime start = null;
        LocalDateTime end = null;

        if(rangeStart != null) {
            start = toLocalDateTime(rangeStart);
        }

        if (rangeEnd != null) {
            end = toLocalDateTime(rangeEnd);
        }
        log.info("attempt to get all events by admin with params: users =  {}, states = {}," +
                "categories = {}, start = {}, end = {}, from = {}, size = {}", Arrays.toString(users),
                Arrays.toString(states), Arrays.toString(categories), start, end, from, size);
        return eventService.getEventsByAdmin(users, states, categories, start, end, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable @Positive Long eventId,
                                @RequestBody @Valid UpdateEventAdminRequest dto) {
        log.info("attempt to update event = {} by admin", eventId);
        return eventService.updateEventByAdmin(eventId,dto);
    }

    private LocalDateTime toLocalDateTime(String s) {
        log.info("attempt to parse {} to localDateTime", s);
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
