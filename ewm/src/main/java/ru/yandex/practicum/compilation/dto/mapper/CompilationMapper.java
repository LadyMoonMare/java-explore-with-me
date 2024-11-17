package ru.yandex.practicum.compilation.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.event.dto.mapper.EventMapper;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static CompilationDto fromCompilationToDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::fromEventToShortDto)
                        .collect(Collectors.toList()))
                .build();
    }

}
