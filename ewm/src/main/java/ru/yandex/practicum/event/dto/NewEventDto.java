package ru.yandex.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.event.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank
    @Max(2000)
    @Min(20)
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Max(7000)
    @Min(20)
    private String description;
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;
    @Valid
    @NotNull
    private LocationDto location;
    private Boolean paid = false;
    private Long participantLimit = 0L;
    private Boolean requestModeration = true;
    @NotBlank
    @Max(120)
    @Min(3)
    private String title;
}
