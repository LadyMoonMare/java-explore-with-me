package ru.yandex.practicum.event.dto.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.event.location.dto.LocationDto;
import ru.yandex.practicum.event.model.State;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Max(2000)
    @Min(20)
    private String annotation;
    private Long category;
    @Max(7000)
    @Min(20)
    private String description;
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;
    @Valid
    private LocationDto location;
    private Boolean paid = false;
    private Long participantLimit = 0L;
    private Boolean requestModeration = true;
    private State stateAction;
    @Max(120)
    @Min(3)
    private String title;
}
