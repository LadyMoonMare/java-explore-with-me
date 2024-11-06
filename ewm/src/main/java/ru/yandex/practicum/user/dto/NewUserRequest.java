package ru.yandex.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @Email
    private String email;
    @NotBlank
    @Min(3)
    @Max(249)
    private String name;
}
