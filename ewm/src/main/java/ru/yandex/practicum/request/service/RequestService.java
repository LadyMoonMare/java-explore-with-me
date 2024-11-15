package ru.yandex.practicum.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    @Transactional
    RequestDto addRequest(Long userId, Long eventId);

    @Transactional
    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getRequestsByUserId(Long userId);

    List<RequestDto> getRequestsByEventId(Long userId, Long eventId);

    @Transactional
    EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                  EventRequestStatusUpdateRequest request);
}
