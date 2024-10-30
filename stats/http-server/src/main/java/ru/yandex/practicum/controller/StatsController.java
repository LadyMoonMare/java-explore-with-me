package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.stats.StatsDto;
import ru.yandex.practicum.dto.stats.StatsViewDto;
import ru.yandex.practicum.exception.InvalidRequestDataException;
import ru.yandex.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
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
        LocalDateTime timeStart = LocalDateTime.parse(start);
        LocalDateTime timeEnd = LocalDateTime.parse(end);

        if (timeStart.isAfter(timeEnd) || timeEnd.isBefore(timeStart)) {
            log.warn("error of time borders");
            throw new InvalidRequestDataException("invalid time");
        }
        log.info("attempt to get all stats");
        return statsService.getStats(timeStart,timeEnd,uris,unique);
    }
}
