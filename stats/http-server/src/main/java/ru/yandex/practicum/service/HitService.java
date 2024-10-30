package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.hit.HitDto;

public interface HitService {
    void hitEndpoint(HitDto dto);
}
