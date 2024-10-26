package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.client.StatsClient;

import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/hit")
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsClient statsClient;

    @GetMapping
    public ResponseEntity<Object> getStats(@RequestHeader("start") @NotBlank String start,
                                           @RequestHeader("end") @NotBlank String end,
                                           @RequestHeader(value = "uris", required = false) String[] uris,
                                           @RequestHeader(value = "unique", defaultValue = "false")
                                           boolean unique) {
        log.info("attempt to get all stats");
        return statsClient.getStats(start, end, uris, unique);
    }
}
