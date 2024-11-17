package ru.yandex.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class NewCompilationDto {
    Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    String title;
    List<Long> events;

    public NewCompilationDto(Boolean pinned, String title, List<Long> events) {
        this.pinned = pinned;
        this.title = title;
        this.events = events;
    }
}
