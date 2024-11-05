package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.stats.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
