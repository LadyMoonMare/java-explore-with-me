package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.yandex.practicum.dto.stats.StatsDto;
import ru.yandex.practicum.dto.stats.StatsViewDto;
import ru.yandex.practicum.mapper.StatsMapper;
import ru.yandex.practicum.model.StatsView;
import ru.yandex.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final Comparator<StatsViewDto> comparator = new Comparator<StatsViewDto>() {
        @Override
        public int compare(StatsViewDto o1, StatsViewDto o2) {
            if ((o1.getHits() - o2.getHits()) > 0) {
                return -1;
            } else if ((o1.getHits() - o2.getHits()) < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    @Override
    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris
            , boolean unique){
        if (!unique) {
            log.info("getting all stats from repo");
            List<StatsView> allStats = statsRepository.getAllStats(start, end);

            if (uris != null) {
                log.info("filter all stats by uri");
                return filterStatsByUris(allStats, uris);
            } else {
                return allStats.stream()
                        .map(StatsMapper::toStatsViewDto)
                        .sorted(comparator)
                        .collect(Collectors.toList());
            }
        } else {
            log.info("getting stats for unique ips from repo");
            List<StatsView> uniqueStats = statsRepository.getUniqueStats(start, end);

            if (uris != null) {
                log.info("filter unique stats by uri");
                return filterStatsByUris(uniqueStats, uris);
            } else {
                return uniqueStats.stream()
                        .map(StatsMapper::toStatsViewDto)
                        .sorted(comparator)
                        .collect(Collectors.toList());
            }
        }
    }

    private List<StatsViewDto> filterStatsByUris(List<StatsView> allStats, String[] uris) {
        List<StatsViewDto> finalList = new ArrayList<>();

        for (String uri : uris) {
            List<StatsViewDto> statsByUri = allStats.stream()
                    .filter(s -> {
                        log.info("s {}, uri {}", s.getUri(), uri);
                        return Objects.equals(s.getUri(), uri);
                    })
                    .map(StatsMapper::toStatsViewDto)
                    .toList();

            log.info("got all stats for uri {}",uri);
            finalList.addAll(statsByUri);
        }

        log.info("sorting final list desc");
        return finalList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
