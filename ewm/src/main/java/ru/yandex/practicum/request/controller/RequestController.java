package ru.yandex.practicum.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.service.RequestService;
import ru.yandex.practicum.request.dto.RequestDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable @Positive Long userId,
                                 @RequestParam @Positive Long eventId) {
        log.info("attempt to add request to event = {}, by user = {}", eventId,userId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long requestId) {
        log.info("attempt to cancel request = {}, by user = {}", requestId,userId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/requests")
    public List<RequestDto> getAllRequestsByUser(@PathVariable @Positive Long userId) {
        log.info("attempt to get all requests by user = {}",userId);
        return requestService.getRequestsByUserId(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<RequestDto> getAllRequestsByEvent(@PathVariable @Positive Long userId,
                                                  @PathVariable @Positive Long eventId) {
        log.info("attempt to get all requests to event = {}, by user = {}", eventId,userId);
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(@PathVariable @Positive Long userId,
                                                               @PathVariable @Positive Long eventId,
                                                               @RequestBody @Valid
                                                                   EventRequestStatusUpdateRequest request) {
        log.info("attempt to update requests to event = {}, by user = {}", eventId,userId);
        return requestService.updateRequests(userId, eventId, request);
    }
}
