package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.update.UpdateEventUserRequest;
import ru.yandex.practicum.event.service.EventService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(@PathVariable @Positive Long userId,
                             @RequestBody @Valid NewEventDto eventDto) {
        log.info("attempt to add event {} by user {}",eventDto.getTitle(), userId);
        return eventService.addEvent(userId, eventDto);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable @Positive Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("attempt to get events by params from {}, size {}", from, size);
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getFullEventById(@PathVariable @Positive Long userId,
                                     @PathVariable @Positive Long eventId) {
        log.info("attempt to get event {} by user {}", eventId, userId);
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable @Positive Long userId,
                                @PathVariable @Positive Long eventId,
                                @RequestBody @Valid UpdateEventUserRequest dto) {
        log.info("attempt to update event {} by user {}", eventId, userId);
        return eventService.updateEventByUser(userId, eventId, dto);
    }
}
