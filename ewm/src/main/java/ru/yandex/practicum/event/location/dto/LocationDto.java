package ru.yandex.practicum.event.location.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @PositiveOrZero
    private Long lat;
    @PositiveOrZero
    private Long lon;
}
