package ru.yandex.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.RequestDto;
import ru.yandex.practicum.request.dto.mapper.RequestMapper;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.RequestState;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        log.info("attempt tp add request");
        Event event = getEventById(eventId);
        User user = getUser(userId);

        if (user.equals(event.getUser())) {
            log.warn("adding request failure");
            throw new ConflictException("Initiator cannot send requests to own event");
        }

        if (requestRepository.findByRequester(user).isPresent()) {
            log.warn("adding request failure");
            throw new ConflictException("Request cannot be send more than once");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("adding request failure");
            throw new ConflictException("Request cannot be send to unpublished event");
        }

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            log.warn("adding request failure");
            throw new ConflictException("Event is unavailable for requests");
        }

        if (event.getRequestModeration().equals(false)) {
            Request request = new Request(LocalDateTime.now(), event, user, RequestState.CONFIRMED);
            requestRepository.save(request);

            setEventRequests(event,1);
            log.info("adding success");
            return RequestMapper.fromRequestToDto(request);
        } else {
            Request request = new Request(LocalDateTime.now(), event, user, RequestState.PENDING);
            requestRepository.save(request);
            log.info("adding success");
            return RequestMapper.fromRequestToDto(request);
        }
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info("attempt to cancel request");
        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("validation failure");
            return new NotFoundException("Request id = " + requestId + "is not found");
        });
        User user = getUser(userId);

        if (request.getState().equals(RequestState.CONFIRMED)) {
            log.info("reducing number of requests for event");
            Event event = getEventById(request.getEvent().getId());
            setEventRequests(event,-1);
        }

        request.setState(RequestState.CANCELED);
        requestRepository.save(request);
        log.info("cancelling success");
        return RequestMapper.fromRequestToDto(request);
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {
        log.info("attempt to get all request by user id = {}", userId);
        User user = getUser(userId);
        return requestRepository.findAllByRequester(user).stream()
                .map(RequestMapper::fromRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Long userId, Long eventId) {
        log.info("attempt to get all requests by user id = {} to event = {}", userId, eventId);
        User user = getUser(userId);
        Event event = getEventById(eventId);
        if (!event.getUser().equals(user)) {
            log.warn("getting requests failure");
            throw new ConflictException("User " + userId + "is not initiator of event " + eventId);
        }

        return requestRepository.findAllByEvent(event).stream()
                .map(RequestMapper::fromRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest request) {
        log.info("attempt to update requests by user id = {} to event = {}", userId, eventId);
        User user = getUser(userId);
        Event event = getEventById(eventId);
        if (!event.getUser().equals(user)) {
            log.warn("getting requests failure");
            throw new ConflictException("User " + userId + "is not initiator of event " + eventId);
        }
        List<Request> allRequests = requestRepository.findAllByEvent(event);
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            log.warn("adding request failure");
            throw new ConflictException("Event is unavailable for requests");
        }

        for (Request r : allRequests) {
            for (Long id : request.getRequestIds()) {
                if (r.getId().equals(id)) {
                    if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
                        request.setStatus(RequestState.REJECTED);
                    }

                    r.setState(request.getStatus());
                    if (request.getStatus().equals(RequestState.CONFIRMED)) {
                        requestRepository.save(r);
                        confirmed.add(r);
                        setEventRequests(event,1);
                    } else {
                        requestRepository.save(r);
                        rejected.add(r);
                    }
                }
            }
        }
        return new EventRequestStatusUpdateResult(
                confirmed.stream().map(RequestMapper::fromRequestToDto).collect(Collectors.toList()),
                rejected.stream().map(RequestMapper::fromRequestToDto).collect(Collectors.toList())
        );
    }

    private void setEventRequests(Event event, int num) {
        if(event.getConfirmedRequests() == null) {
            event.setConfirmedRequests(0L);
        }

        event.setConfirmedRequests(event.getConfirmedRequests() + num);
        eventRepository.save(event);
    }

    private Event getEventById(Long eventId) {
        log.info("getting event with id {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("getting event failure");
            return new NotFoundException("Event id =" + eventId + "is not found");
        });
    }

    private User getUser(Long userId) {
        log.info("validation by initiator id {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("validation failure");
            return new NotFoundException("User id =" + userId + "is not found");
        });
    }
}
