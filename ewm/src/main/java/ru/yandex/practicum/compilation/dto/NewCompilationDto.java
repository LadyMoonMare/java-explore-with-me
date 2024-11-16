package ru.yandex.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
public class NewCompilationDto {
    Boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50)
    String title;
    List<Long> events;

    public NewCompilationDto(Boolean pinned, String title, List<Long> events) {
        this.pinned = Objects.requireNonNullElse(pinned, false);
        this.title = title;
        this.events = events;
    }
}
