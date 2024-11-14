package ru.yandex.practicum.event.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.update.UpdateEventAdminRequest;
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
        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .participantLimit(dto.getParticipantLimit())
                .eventDate(dto.getEventDate())
                .paid(dto.getPaid())
                .createdOn(LocalDateTime.now())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .location(location)
                .state(State.PENDING)
                .category(category)
                .user(initiator)
                .build();
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
        EventDto eventDto = EventDto.builder()
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
                .createdOn(event.getCreatedOn())
                .requestModeration(event.getRequestModeration())
                .views(event.getViews())
                .build();

        if (eventDto.getState().equals(State.PUBLISHED)) {
            eventDto.setPublishedOn(LocalDateTime.now());
        }

        return eventDto;
    }

    public static UpdateEventUserRequest fromAdminRequestToUserRequest(UpdateEventAdminRequest dto) {
        return UpdateEventUserRequest.builder()
                .category(dto.getCategory())
                .location(dto.getLocation())
                .eventDate(dto.getEventDate())
                .paid(dto.getPaid())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .participantLimit(dto.getParticipantLimit())
                .stateAction(dto.getStateAction())
                .build();
    }
}
