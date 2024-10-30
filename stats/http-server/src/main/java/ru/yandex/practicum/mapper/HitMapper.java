package ru.yandex.practicum.mapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.hit.HitDto;
import ru.yandex.practicum.model.Hit;

import java.time.LocalDateTime;

@UtilityClass
public class HitMapper {
    public static Hit fromHitDto(HitDto dto) {
        return new Hit(dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp());
    }

    public static HitDto makeHitDto(HttpServletRequest request) {
        return HitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
