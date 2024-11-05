package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.StatsView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsRepository extends JpaRepository<StatsView,Long> {
    Optional<StatsView> findByAppAndUri(String app, String uri);

    @Query(value = "select s.id, s.app, s.uri, s.hits from view_stats s " +
            "join endpoint_hits as h on s.uri = h.uri " +
            "where (s.app = h.app) and (h.time_stamp > ?1) and (h.time_stamp < ?2) " +
            "group by s.id", nativeQuery = true)
    List<StatsView> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query(value = "select s.id, s.app, s.uri, count(distinct h.ip) as hits from view_stats s " +
            "join endpoint_hits as h on s.uri = h.uri " +
            "where (s.app = h.app) and (h.time_stamp > ?1) and (h.time_stamp < ?2) " +
            "group by s.id", nativeQuery = true)
    List<StatsView> getUniqueStats(LocalDateTime start,LocalDateTime end);
}
