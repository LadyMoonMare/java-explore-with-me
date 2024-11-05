package ru.yandex.practicum.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDto {
    private String app;
    private List<String> uri;
    private Long hits;
}
