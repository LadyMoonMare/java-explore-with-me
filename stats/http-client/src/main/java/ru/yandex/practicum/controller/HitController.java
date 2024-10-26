package ru.yandex.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.client.HitClient;
import ru.yandex.practicum.hit.HitDto;

import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/hit")
@RequiredArgsConstructor
@Slf4j
public class HitController {
    private final HitClient hitClient;

    @PostMapping
    public ResponseEntity<Object> hitEndpoint(HttpServletRequest request) {
        HitDto hit = makeHitDto(request);
        log.info("request to add info about hitting endpoint {}", hit);
        return hitClient.hitEndpoint(hit);
    }

    private HitDto makeHitDto(HttpServletRequest request) {
      return HitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
