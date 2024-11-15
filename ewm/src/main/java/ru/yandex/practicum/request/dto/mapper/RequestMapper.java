package ru.yandex.practicum.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.request.dto.RequestDto;
import ru.yandex.practicum.request.model.Request;

@UtilityClass
public class RequestMapper {
    public static RequestDto fromRequestToDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getState())
                .build();
    }
}
