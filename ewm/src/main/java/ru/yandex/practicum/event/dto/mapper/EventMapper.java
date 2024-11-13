package ru.yandex.practicum.event.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.update.UpdateEventUserRequest;
import ru.yandex.practicum.event.location.dto.LocationDto;
import ru.yandex.practicum.event.location.model.Location;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.user.dto.UserShortDto;
import ru.yandex.practicum.user.dto.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public static Event fromNewEventDtoToEvent(NewEventDto dto, Category category, User initiator,
                                               Location location) {
        Event event = Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .participantLimit(dto.getParticipantLimit())
                .eventDate(dto.getEventDate())
                .paid(dto.getPaid())
                .publishedOn(LocalDateTime.now())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .location(location)
                .category(category)
                .user(initiator)
                .build();
        if (dto.getRequestModeration()) {
            event.setState(State.SEND_TO_REVIEW);
        } else {
            event.setState(State.PUBLISHED);
        }
        return event;
    }

    public static EventShortDto fromEventToShortDto(Event event) {
        CategoryDto category = CategoryMapper.fromCategoryToDto(event.getCategory());
        UserShortDto user = UserMapper.fromUserToShortDto(event.getUser());
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .eventDate(event.getEventDate())
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(user)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventDto fromEventToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.fromCategoryToDto(event.getCategory()))
                .state(event.getState())
                .eventDate(event.getEventDate())
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserMapper.fromUserToShortDto(event.getUser()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .location(new LocationDto(event.getLocation().getLat(),event.getLocation().getLon()))
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .views(event.getViews())
                .build();
    }

    public static Event fromUserUpdateRequestToEvent(UpdateEventUserRequest dto) {
        return Event.builder()
                .eventDate(dto.getEventDate())
                .paid(dto.getPaid())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .participantLimit(dto.getParticipantLimit())
                .state(dto.getStateAction())
                .build();
    }
}
