package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.stats.StatsViewDto;
import ru.yandex.practicum.exception.InvalidRequestDataException;
import ru.yandex.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @GetMapping
    public List<StatsViewDto> getStats(@RequestParam("start") String start,
                                       @RequestParam("end") String end,
                                       @RequestParam(value = "uris", required = false) String[] uris,
                                       @RequestParam(value = "unique", defaultValue = "false")
                                       boolean unique) {
        LocalDateTime timeStart = toLocalDateTime(start);
        LocalDateTime timeEnd = toLocalDateTime(end);

        if (timeStart.isAfter(timeEnd) || timeEnd.isBefore(timeStart)) {
            log.warn("error of time borders");
            throw new InvalidRequestDataException("invalid time");
        }
        log.info("attempt to get all stats for uris {}", Arrays.toString(uris));
        return statsService.getStats(timeStart,timeEnd,uris,unique);
    }

    private LocalDateTime toLocalDateTime(String s) {
        log.info("attempt to parse {} to localDateTime", s);
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
