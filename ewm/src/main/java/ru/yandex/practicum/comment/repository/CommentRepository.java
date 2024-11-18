package ru.yandex.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.model.User;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Optional<Comment> findByAuthorAndEvent(User author, Event event);
}
