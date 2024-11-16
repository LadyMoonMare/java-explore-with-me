package ru.yandex.practicum.compilation.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable @Positive Long compId) {
        log.info("attempt to get compilation {}", compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam Boolean pinned,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "10")
                                                    @Positive Integer size) {
        log.info("attempt to get all compilations by param pinned {}", pinned);
        return compilationService.getCompilations(pinned, from, size);
    }
}
