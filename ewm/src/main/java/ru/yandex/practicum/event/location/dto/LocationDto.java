package ru.yandex.practicum.event.location.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull
    private Long lat;
    @NotNull
    private Long lon;
}
