package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.service.EventServiceImpl;

@RestController
@Slf4j
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventServiceImpl eventService;

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable @Positive Long id, HttpServletRequest request) {
        log.info("attempt to get event {} from public", id);
        return eventService.getEventPublic(id, request);
    }
}
