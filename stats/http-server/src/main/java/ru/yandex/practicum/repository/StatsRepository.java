package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.StatsView;

import java.util.Optional;

public interface StatsRepository extends JpaRepository<StatsView,Long> {
    Optional<StatsView> findByAppAndUri(String app, String uri);

}
