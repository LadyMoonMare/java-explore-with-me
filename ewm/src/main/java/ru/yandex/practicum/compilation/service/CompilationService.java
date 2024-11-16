package ru.yandex.practicum.compilation.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    @Transactional
    CompilationDto addNewCompilation(NewCompilationDto dto);

    @Transactional
    void deleteCompilation(Long compId);

    @Transactional
    CompilationDto updateCompilation(UpdateCompilationRequest dto, Long compId);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
