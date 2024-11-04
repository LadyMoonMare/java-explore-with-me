package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.dto.hit.HitDto;
import ru.yandex.practicum.service.HitService;

@Controller
@RequestMapping(path = "/hit")
@RequiredArgsConstructor
@Slf4j
public class HitController {
    private final HitService hitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void hitEndpoint(@Valid HitDto dto) {
        log.info("dto app{}, ip {}, uri {}, timestamp {}", dto.getApp(), dto.getIp()
                , dto.getUri(), dto.getTimestamp());
        log.info("attempt to add endpoint to server");
        hitService.hitEndpoint(dto);
    }
}
