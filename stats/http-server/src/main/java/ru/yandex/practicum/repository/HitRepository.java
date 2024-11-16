package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Hit;

import java.util.Optional;

public interface HitRepository extends JpaRepository<Hit,Long> {
    Optional<Hit> findByIp(String ip);
}
