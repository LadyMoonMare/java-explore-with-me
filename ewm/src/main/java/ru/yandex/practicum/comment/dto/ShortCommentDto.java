package ru.yandex.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortCommentDto {
    private Long id;
    private String author;
    private String description;
    private LocalDateTime published;
    private Long event;
}
