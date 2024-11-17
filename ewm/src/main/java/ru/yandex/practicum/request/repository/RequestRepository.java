package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequester(User requester);

    List<Request> findAllByRequester(User requester);

    List<Request> findAllByEvent(Event event);
}
