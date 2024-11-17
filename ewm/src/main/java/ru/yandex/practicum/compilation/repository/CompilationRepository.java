package ru.yandex.practicum.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.compilation.model.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation,Long> {
    Optional<Compilation> findByTitle(String title);

    @Query(value = "select * from compilations limit ?2 offset ?1", nativeQuery = true)
    List<Compilation> getAllButLimit(Integer from, Integer size);

    @Query(value = "select * from compilations " +
            "where pinned = ?3 " +
            "limit ?2 offset ?1", nativeQuery = true)
    List<Compilation> getAllButLimitAndPinned(Integer from, Integer size, Boolean pinned);
}
