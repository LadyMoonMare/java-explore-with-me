package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.stats.StatsViewDto;
import ru.yandex.practicum.model.StatsView;

public class StatsMapper {
    public static StatsViewDto toStatsViewDto(StatsView statsView) {
        return StatsViewDto.builder()
                .app("ewm-main-service")
                .uri(statsView.getUri())
                .hits(statsView.getHits())
                .build();
    }
}
