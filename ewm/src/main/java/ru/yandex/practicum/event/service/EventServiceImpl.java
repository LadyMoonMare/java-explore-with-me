package ru.yandex.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.client.HitClient;
import ru.yandex.practicum.dto.hit.HitDto;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.mapper.EventMapper;
import ru.yandex.practicum.event.dto.update.UpdateEventAdminRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final HitClient hitClient;
    private final Comparator<Event> comparator = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return (int) (o2.getId() - o1.getId());
        }
    };
    private Set<String> uniqueIp = new HashSet<>();

    @Override
    @Transactional
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

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        log.info("attempt to get events from repo by user {}", userId);
        User user = getUser(userId);
        log.info("getting all events from repo");
        return eventRepository.findAllByInitiatorButLimit(from, size, userId).stream()
                .map(EventMapper::fromEventToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEvent(Long userId, Long eventId) {
        User user = getUser(userId);
        log.info("attempt to get event with id {}", eventId);
        Event event = getEventById(eventId);
        log.info("getting event success");
        return EventMapper.fromEventToDto(event);
    }

    @Override
    @Transactional
    public EventDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.info("attempt to update event {} by user", eventId);
        User user = getUser(userId);
        Event event = getEventById(eventId);

        if (!event.getUser().getId().equals(user.getId())) {
            log.warn("update failure");
            throw new ConflictException("User is not owner of this event");
        }

        if (event.getState().equals(State.PUBLISHED)) {
            log.warn("failure");
            throw new ConflictException("Published event cannot be changed");
        }

        if (dto.getStateAction() != null) {
            log.info("new state {}", dto.getStateAction());
            if (dto.getStateAction().equals(State.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            } else {
                event.setState(State.PENDING);
            }
        }

        if (dto.getEventDate() != null) {
            log.info("new date {}", dto.getEventDate());
            isEventDateValid(dto.getEventDate());
            event.setEventDate(dto.getEventDate());
        }

        setEventFields(dto, event);
        event = eventRepository.save(event);
        log.info("updating success");
        return EventMapper.fromEventToDto(event);
    }

    @Override
    public List<EventDto> getEventsByAdmin(Long[] users, String[] states, Long[] categories,
                                           LocalDateTime start, LocalDateTime end, Integer from,
                                           Integer size) {
        log.info("getting all events from repo");
        List<Event> allEvents;
        if (start == null && end == null) {
            allEvents = eventRepository.findAllButLimit(from,size);
        } else {
            if (start != null && end != null) {
                allEvents = eventRepository.findAllButLimitAndTime(from, size, start, end);
            } else if (start != null) {
                allEvents = eventRepository.findAllButLimitAndStart(from, size, start);
            } else {
                allEvents = eventRepository.findAllButLimitAndEnd(from, size, end);
            }
        }
        List<Event> finalList = new ArrayList<>();

        log.info("sorting all events by params");
        if (users != null) {
            for (Long user : users) {
                List<Event> sublist = allEvents.stream()
                        .filter(e -> Objects.equals(e.getUser().getId(), user))
                        .toList();
                finalList.addAll(sublist);
            }
        }

        if (categories != null) {
            for (Long category : categories) {
                List<Event> sublist = allEvents.stream()
                        .filter(e -> Objects.equals(e.getCategory().getId(), category))
                        .toList();
                finalList.addAll(sublist);
            }
        }

        if (states != null) {
            for (String state : states) {
                List<Event> sublist = allEvents.stream()
                        .filter(e -> Objects.equals(e.getState(), State.valueOf(state)))
                        .toList();
                finalList.addAll(sublist);
            }
        }

        if (finalList.isEmpty()) {
            finalList = allEvents;
        }
        return new HashSet<>(finalList).stream()
                .sorted(comparator)
                .map(EventMapper::fromEventToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        log.info("attempt to update event {} by admin", eventId);
        Event event = getEventById(eventId);

        if (dto.getStateAction() != null) {
            if (!(event.getState().equals(State.PENDING) &&
                    (dto.getStateAction().equals(State.REJECT_EVENT) ||
                            dto.getStateAction().equals(State.PUBLISH_EVENT)))) {
                log.warn("updating failure");
                throw new ConflictException("Invalid state");
            } else {
                log.info("new state {}", dto.getStateAction());
                if (dto.getStateAction().equals(State.REJECT_EVENT)) {
                    event.setState(State.CANCELED);
                } else {
                    event.setState(State.PUBLISHED);
                }
            }
        }

        if (event.getState().equals(State.PUBLISHED)) {
            event.setPublishedOn(LocalDateTime.now());
        }

        if (dto.getEventDate() != null) {
            log.info("new date {}", dto.getEventDate());
            event.setEventDate(dto.getEventDate());
        }

        setEventFields(EventMapper.fromAdminRequestToUserRequest(dto), event);
        event = eventRepository.save(event);
        log.info("updating success");
        return EventMapper.fromEventToDto(event);

    }

    @Override
    @Transactional
    public EventDto getEventPublic(Long id, HttpServletRequest request) {
        log.info("attempt to update event {} by public", id);
        Event event = getEventById(id);

        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("getting forbidden");
            throw new NotFoundException("Event " + id + " is unpublished");
        }

        if (event.getViews() == null) {
            event.setViews(0L);
        }

        log.info("adding statistics");
        hitClient.hitEndpoint(HitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());

        if (!uniqueIp.contains(request.getRemoteAddr())) {
            event.setViews(event.getViews() + 1);
            event = eventRepository.save(event);
            uniqueIp.add(request.getRemoteAddr());
        }
        return EventMapper.fromEventToDto(event);
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, Long[] categories, Boolean paid,
                                               LocalDateTime start, LocalDateTime end,
                                               Boolean onlyAvailable, String sort, Integer from,
                                               Integer size, HttpServletRequest request) {
        List<Event> allEvents;
        log.info("getting all events from repo");
        if (end == null) {
            allEvents = eventRepository.findAllButLimitAndStart(from, size, start);
        } else {
            log.info("getting all events from repo");
            if (start.isAfter(end)) {
                log.warn("getting events failure");
                throw new ValidationException("Invalid range");
            }
            allEvents = eventRepository.findAllButLimitAndTime(from, size, start, end);
        }
        List<Event> finalList = sortEventsForPublic(allEvents, categories, paid, onlyAvailable,
                sort, text);

        log.info("adding statistics");
        hitClient.hitEndpoint(HitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());

        return finalList.stream()
                .sorted(comparator)
                .map(EventMapper::fromEventToShortDto)
                .collect(Collectors.toList());
    }

    private List<Event> sortEventsForPublic(List<Event> allEvents, Long[] categories, Boolean paid,
                                            Boolean onlyAvailable, String sort, String text) {
        log.info("sorting all events");
        List<Event> finalList = new ArrayList<>();

        if (onlyAvailable) {
            log.info("sorting by available");

            List<Event> sublist = allEvents.stream()
                    .filter(e -> !e.getParticipantLimit().equals(e.getConfirmedRequests()))
                    .toList();
            finalList.addAll(sublist);
        }

        if (text != null && !text.isBlank()) {
            log.info("sorting by text {}", text);
            List<Event> sublist = allEvents.stream()
                    .filter(e -> e.getAnnotation().toLowerCase().contains(text.toLowerCase()) ||
                            e.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .toList();
            finalList.addAll(sublist);
        }

        if (paid != null) {
            log.info("sorting by paid {}", paid);
            List<Event> sublist = allEvents.stream()
                    .filter(e -> {
                        if (paid) {
                            return e.getPaid().equals(true);
                        } else {
                            return e.getPaid().equals(false);
                        }
                    })
                    .toList();
            finalList.addAll(sublist);
        }

        if (categories != null) {
            log.info("sorting by categories {}", Arrays.toString(categories));
            for (Long id : categories) {
                List<Event> sublist = allEvents.stream()
                        .filter(e -> e.getCategory().getId().equals(id))
                        .toList();
                finalList.addAll(sublist);
            }
        }

         if (finalList.isEmpty()) {
             log.info("no special filters result = all events by time");
             finalList = allEvents;
         }

         if (sort != null) {
             log.info("sort {}",sort);
             if (sort.equals("VIEWS")) {
                 log.info("sorting by views");
                 finalList.sort(new Comparator<Event>() {
                     @Override
                     public int compare(Event o1, Event o2) {
                         return (int) (o1.getViews() - o2.getViews());
                     }
                 });
             } else if (sort.equals("EVENT_DATE")) {
                 log.info("sorting by date");
                 finalList.sort(new Comparator<Event>() {
                     @Override
                     public int compare(Event o1, Event o2) {
                         if (o1.getEventDate().isAfter(o2.getEventDate())) {
                             return 1;
                         } else if (o1.getEventDate().isBefore(o2.getEventDate())) {
                             return -1;
                         } else {
                             return 0;
                         }
                     }
                 });
             }
         }
         return new HashSet<>(finalList).stream().toList();
    }

    private void setEventFields(UpdateEventUserRequest dto, Event event) {
        log.info("setting new fields");

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
        }
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
            throw new ValidationException("Invalid event date. Date must be after two hours from now");
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