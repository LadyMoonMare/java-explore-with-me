package ru.yandex.practicum.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.comment.model.CommentState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminDto {
    @NotNull
    private CommentState state;
}
