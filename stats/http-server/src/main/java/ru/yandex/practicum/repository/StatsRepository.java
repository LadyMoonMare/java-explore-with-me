package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.StatsView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsRepository extends JpaRepository<StatsView,Long> {
    Optional<StatsView> findByAppAndUri(String app, String uri);

    @Query(value = "select s from StatsView s " +
            "join Hit as h on s.uri = h.uri " +
            "where (s.app = h.app) and (h.timestamp > ?1) and (h.time_stamp < ?2)", nativeQuery = true)
    List<StatsView> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query(value = "select s.app, s.uri, count(distinct h.ip) from StatsView s " +
            "join Hit as h on s.uri = h.uri " +
            "where (s.app = h.app) and (h.timestamp > ?1) and (h.time_stamp < ?2)", nativeQuery = true)
    List<StatsView> getUniqueStats(LocalDateTime start,LocalDateTime end);
}
