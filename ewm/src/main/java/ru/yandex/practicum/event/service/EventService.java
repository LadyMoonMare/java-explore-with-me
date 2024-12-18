package ru.yandex.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.update.UpdateEventAdminRequest;
import ru.yandex.practicum.event.dto.update.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventDto addEvent(Long userId, NewEventDto dto);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventDto getEvent(Long userId, Long eventId);

    EventDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    List<EventDto> getEventsByAdmin(Long[] users, String[] states, Long[] categories,
                                    LocalDateTime start, LocalDateTime end, Integer from,
                                    Integer size);

    EventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    EventDto getEventPublic(Long id, HttpServletRequest request);

    List<EventShortDto> getEventsPublic(String text, Long[] categories, Boolean paid,
                                        LocalDateTime start, LocalDateTime end,
                                        Boolean onlyAvailable, String sort, Integer from,
                                        Integer size, HttpServletRequest request);
}
