package ru.yandex.practicum.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsViewDto {
    private String app;
    private String uri;
    private Long hits;
}
