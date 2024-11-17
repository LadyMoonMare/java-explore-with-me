package ru.yandex.practicum.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Size(min = 6,max = 254)
    @Email
    private String email;
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
