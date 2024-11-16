package ru.yandex.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Long> {
    @Query(value = "select * from events " +
            "where category_id = ?1", nativeQuery = true)
    Optional<Event> findByCategory_id(Long category_id);

    @Query(value = "select * from events e " +
            "where e.event_date > ?3 and e.event_date < ?4 " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Event> findAllButLimitAndTime(Integer from, Integer size, LocalDateTime start, LocalDateTime end);

    @Query(value = "select * from events e " +
            "where e.initiator_id = ?3 " +
            "limit ?2 offset ?1", nativeQuery = true)
    List<Event> findAllByInitiatorButLimit(Integer from, Integer size, Long userId);

    @Query(value = "select * from events e " +
            "limit ?2 offset ?1", nativeQuery = true)
    List<Event> findAllButLimit(Integer from, Integer size);

    @Query(value = "select * from events e " +
            "where e.event_date > ?3 " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Event> findAllButLimitAndStart(Integer from, Integer size, LocalDateTime start);

    @Query(value = "select * from events e " +
            "where e.event_date < ?3 " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Event> findAllButLimitAndEnd(Integer from, Integer size, LocalDateTime end);

    List<Event> findByIdIn(List<Long> ids);
}
