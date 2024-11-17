package ru.yandex.practicum.event.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.event.location.model.Location;

public interface LocationRepository extends JpaRepository<Location,Long> {
}
