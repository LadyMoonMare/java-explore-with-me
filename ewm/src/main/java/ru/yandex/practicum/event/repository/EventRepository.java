package ru.yandex.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Long> {
    @Query(value = "select * from events " +
            "where category_id = ?1", nativeQuery = true)
    Optional<Event> findByCategory_id(Long category_id);

    @Query(value = "select * from events e " +
            "join category as c on e.category_id = c.id " +
            "join app_users as u on e.initiator_id = u.id " +
            "join location as l on e.location_id = l.id " +
            "group by e.id " +
            "limit ?2 offset ?1", nativeQuery = true)
    List<Event> findAllButLimit(Integer from, Integer size);

    @Query(value = "select * from events e " +
            "where e.initiator_id = ?3 " +
            "limit ?2 offset ?1", nativeQuery = true)
    List<Event> findAllByInitiatorButLimit(Integer from, Integer size, Long userId);
}
