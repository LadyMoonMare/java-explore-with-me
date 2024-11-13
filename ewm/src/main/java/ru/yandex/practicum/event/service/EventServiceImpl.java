package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.mapper.EventMapper;
import ru.yandex.practicum.event.dto.update.UpdateEventUserRequest;
import ru.yandex.practicum.event.location.model.Location;
import ru.yandex.practicum.event.location.repository.LocationRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public EventDto addEvent(Long userId, NewEventDto dto) {
        log.info("attempt to add event to repo");
        isEventDateValid(dto.getEventDate());
        log.info("validation by user and category");
        User initiator = getUser(userId);
        Category category = getCategory(dto.getCategory());
        Location location = locationRepository.save(new Location(dto.getLocation().getLat(),
                dto.getLocation().getLon()));
        Event event = EventMapper.fromNewEventDtoToEvent(dto, category, initiator, location);
        eventRepository.save(event);
        return EventMapper.fromEventToDto(event);
    }

    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        log.info("attempt to get events from repo by user {}", userId);
        User user = getUser(userId);
        log.info("getting all events from repo");
        return eventRepository.findAllByInitiatorButLimit(from, size, userId).stream()
                .map(EventMapper::fromEventToShortDto)
                .collect(Collectors.toList());
    }

    public EventDto getEvent(Long userId, Long eventId) {
        User user = getUser(userId);
        log.info("attempt to get event with id {}", eventId);
        Event event = getEventById(eventId);
        log.info("getting event success");
        return EventMapper.fromEventToDto(event);
    }

    public EventDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.info("attempt to update event {}", eventId);
        User user = getUser(userId);
        Event event = getEventById(eventId);
        if (!event.getUser().equals(user)) {
            log.warn("updating failure");
            throw new ConflictException("User id = " + userId + " is not initiator of event id = "
                    + eventId);
        }
        event = eventRepository.save(event);
        log.info("updating success");
        return EventMapper.fromEventToDto(event);
    }

    private Event setEventFields(UpdateEventUserRequest dto, Event event) {
        log.info("setting new fields");
        if (dto.getStateAction() != null && !(dto.getStateAction().equals(State.CANCEL_REVIEW)
                || dto.getStateAction().equals(State.SEND_TO_REVIEW))) {
            log.warn("updating failure");
            throw new ConflictException("Updating published event is forbidden");
        } else if (dto.getStateAction() != null) {
            log.info("new state {}", dto.getStateAction());
            event.setState(dto.getStateAction());
        }

        if (dto.getCategory() != null) {
            log.info("new category {}", dto.getCategory());
            Category category = getCategory(dto.getCategory());
            event.setCategory(category);
        }

        if (dto.getLocation() != null) {
            log.info("new location {}", dto.getLocation());
            Location location = locationRepository.save(new Location(dto.getLocation().getLat(),
                    dto.getLocation().getLon()));
            event.setLocation(location);
        }

        if (dto.getEventDate() != null) {
            log.info("new date {}", dto.getEventDate());
            isEventDateValid(dto.getEventDate());
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getAnnotation() != null) {
            log.info("new annotation {}", dto.getAnnotation());
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null) {
            log.info("new description {}", dto.getDescription());
            event.setDescription(dto.getDescription());
        }

        if (dto.getTitle() != null) {
            log.info("new title {}", dto.getTitle());
            event.setTitle(dto.getTitle());
        }

        if (dto.getPaid() != null) {
            log.info("new paid {}", dto.getPaid());
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            log.info("new limit {}", dto.getParticipantLimit());
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            log.info("new moderation {}", dto.getRequestModeration());
            event.setRequestModeration(dto.getRequestModeration());
            if (!dto.getRequestModeration()) {
                event.setState(State.PUBLISHED);
            }
        }
        return event;
    }

    private Event getEventById(Long eventId) {
        log.info("getting event with id {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("getting event failure");
            return new NotFoundException("Event id =" + eventId + "is not found");
        });
    }

    private void isEventDateValid(LocalDateTime date) {
        log.info("validation by event date {}", date);
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("adding event failure");
            throw new ConflictException("Invalid event date. Date must be after two hours from now");
        }
    }

    private User getUser(Long userId) {
        log.info("validation by initiator id {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("validation failure");
            return new NotFoundException("User id =" + userId + "is not found");
        });
    }

    private Category getCategory(Long categoryId) {
        log.info("validation by category id {}", categoryId);
        return categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.warn("validation failure");
            return new NotFoundException("Category id =" + categoryId + "is not found");
        });
    }
}
