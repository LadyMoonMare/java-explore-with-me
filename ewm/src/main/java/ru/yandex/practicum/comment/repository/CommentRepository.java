package ru.yandex.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.comment.model.CommentState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query(value = "select * from comments c " +
            "where c.author_id = ?1 and c.event_id = ?2 ", nativeQuery = true)
    Optional<Comment> findByAuthorAndEvent(Long author, Long event);

    @Query(value = "select * from comments c " +
            "where c.created_on > ?3 and c.created_on < ?4 " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Comment> findAllButLimitAndTime(Integer from, Integer size,
                                         LocalDateTime start, LocalDateTime end);

    @Query(value = "select * from comments c " +
            "where c.created_on > ?3 " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Comment> findAllButLimitAndStart(Integer from, Integer size, LocalDateTime start);

    @Query(value = "select * from comments c " +
            "where c.created_on < ?3 " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Comment> findAllButLimitAndEnd(Integer from, Integer size, LocalDateTime end);

    @Query(value = "select * from comments c " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Comment> findAllButLimit(Integer from, Integer size);

    @Query(value = "select * from comments c " +
            "where c.event_id = ?3 and c.state = ?4 " +
            "limit ?2 offset ?1 ", nativeQuery = true)
    List<Comment> findAllByEventAndStateButLimit(Long eventId, Integer from,
                                         Integer size, CommentState state);
}
