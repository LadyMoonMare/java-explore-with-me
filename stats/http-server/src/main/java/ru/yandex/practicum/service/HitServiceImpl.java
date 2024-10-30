package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.hit.HitDto;
import ru.yandex.practicum.mapper.HitMapper;
import ru.yandex.practicum.model.Hit;
import ru.yandex.practicum.model.StatsView;
import ru.yandex.practicum.repository.HitRepository;
import ru.yandex.practicum.repository.StatsRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final StatsRepository statsRepository;

    @Override
    public void hitEndpoint(HitDto dto) {
        Hit hit = HitMapper.fromHitDto(dto);
        log.info("adding hit {} to repository", hit);
        hitRepository.save(hit);

        log.info("attempt to update statistics");
        Optional<StatsView> statsView = statsRepository.findByAppAndUri(hit.getApp(),hit.getUri());
        if (statsView.isPresent()) {
            log.info("updating statistic of hits");
            StatsView stats = statsView.get();
            stats.setHits(stats.getHits() + 1);
            statsRepository.save(stats);
        } else {
            log.info("there is no such statistic. adding new one ");
            StatsView stats = new StatsView(hit.getApp(), hit.getUri(), 1L);
            statsRepository.save(stats);
        }
    }
}
