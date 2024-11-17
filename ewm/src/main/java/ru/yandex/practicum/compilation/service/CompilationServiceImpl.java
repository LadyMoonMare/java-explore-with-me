package ru.yandex.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilation.dto.mapper.CompilationMapper;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto dto) {
        log.info("attempt to add new compilation to repo");
        if (compilationRepository.findByTitle(dto.getTitle()).isPresent()) {
            log.warn("adding failure");
            throw new ConflictException("Compilation with title " + dto.getTitle() + "is already exist");
        }

        List<Event> events = eventRepository.findByIdIn(dto.getEvents());
        Compilation compilation = new Compilation(dto.getPinned(), dto.getTitle(), events);
        compilation = compilationRepository.save(compilation);
        log.info("adding success");
        return CompilationMapper.fromCompilationToDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("attempt to delete compilation = {} from repo", compId);
        Compilation compilation = getCompilation(compId);
        compilationRepository.delete(compilation);
        log.info("deleting success");
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest dto, Long compId) {
        log.info("attempt to update compilation {}", compId);
        Compilation compilation = getCompilation(compId);

        if (dto.getPinned() != null) {
            log.info("new pinned {}", dto.getPinned());
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getTitle() != null) {
            if (compilationRepository.findByTitle(dto.getTitle()).isPresent()) {
                log.warn("adding failure");
                throw new ConflictException("Compilation with title " + dto.getTitle() + "is already exist");
            }

            log.info("new title {}", dto.getTitle());
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getEvents() != null) {
            log.info("new events {}", dto.getEvents());
            List<Event> events = eventRepository.findByIdIn(dto.getEvents());
            compilation.setEvents(events);
        }

        compilation = compilationRepository.save(compilation);
        return CompilationMapper.fromCompilationToDto(compilation);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return CompilationMapper.fromCompilationToDto(getCompilation(compId));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("attempt to get all compilations");
        if (pinned != null) {
            log.info("getting compilations by pinned");
            return compilationRepository.getAllButLimitAndPinned(from,size,pinned).stream()
                    .map(CompilationMapper::fromCompilationToDto)
                    .collect(Collectors.toList());
        } else {
            log.info("getting compilations without filter");
            return compilationRepository.getAllButLimit(from, size).stream()
                    .map(CompilationMapper::fromCompilationToDto)
                    .collect(Collectors.toList());
        }
    }

    private Compilation getCompilation(Long compId) {
        log.info("attempt to get compilation {} from repo", compId);
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.warn("getting compilation failure");
            return new NotFoundException("Compilation " + compId + " is not found");
        });
    }
}
