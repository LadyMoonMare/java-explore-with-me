package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.yandex.practicum.dto.stats.StatsDto;
import ru.yandex.practicum.dto.stats.StatsViewDto;
import ru.yandex.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris
            , boolean unique){
        if (unique) {

        } else {

        }
        return null;
    }
}
