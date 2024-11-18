package ru.yandex.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.dto.mapper.CommentMapper;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.comment.model.CommentState;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.model.RequestState;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto dto) {
        log.info("attempt to add comment to repo");
        User author = getUser(userId);
        Event event = getEvent(eventId);

        log.info("validation");
        if (author.getId().equals(event.getUser().getId())) {
            log.warn("validation failure");
            throw new ConflictException("author cannot write comments to own events");
        }

        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            log.warn("validation failure");
            throw new ConflictException("Event must be done");
        }

        if (requestRepository.findByRequester(author).isEmpty() ||
                !requestRepository.findByRequester(author).get().getEvent().getId().equals(eventId) ||
                !requestRepository.findByRequester(author).get().getState()
                        .equals(RequestState.CONFIRMED)) {
            log.warn("validation failure");
            throw new ConflictException("User must be participant of event");
        }

        if (commentRepository.findByAuthorAndEvent(author,event).isPresent()) {
            log.warn("validation failure");
            throw new ConflictException("Only one comment is acceptable");
        }

        Comment comment = new Comment(author, dto.getDescription(), CommentState.PENDING,
                LocalDateTime.now(), event);
        comment = commentRepository.save(comment);
        log.info("adding success");
        return CommentMapper.fromCommentToDto(comment);
    }

    @Transactional
    public CommentDto updateCommentByUser(Long userId, Long eventId, Long commentId, NewCommentDto dto) {
        log.info("attempt to update comment {}", commentId);
        User author = getUser(userId);
        Event event = getEvent(eventId);
        Comment comment = getComment(commentId);

        validateComment(author,event,comment);

        comment.setDescription(dto.getDescription());
        comment.setState(CommentState.PENDING);
        comment.setPublished(null);
        comment = commentRepository.save(comment);
        log.info("updating success");
        return CommentMapper.fromCommentToDto(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        log.info("attempt to delete comment {}", commentId);
        User author = getUser(userId);
        Event event = getEvent(eventId);
        Comment comment = getComment(commentId);

        validateComment(author,event,comment);
        commentRepository.delete(comment);
        log.info("deleting success");
    }

    public CommentDto getCommentById(Long userId, Long eventId, Long commentId) {
        log.info("attempt to get comment {}", commentId);

        User author = getUser(userId);
        Event event = getEvent(eventId);
        Comment comment = getComment(commentId);
        validateComment(author,event,comment);

        return CommentMapper.fromCommentToDto(comment);
    }

    private void validateComment(User author, Event event, Comment comment) {
        log.info("validation");
        if (!author.getId().equals(comment.getAuthor().getId()) ||
                !event.getId().equals(comment.getEvent().getId())) {
            log.warn("validation failure");
            throw new ConflictException("Comment must relay to user and event");
        }
    }

    private Comment getComment(Long commentId) {
        log.info("getting comment with id {}", commentId);
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("validation failure");
            return new NotFoundException("Comment id =" + commentId + "is not found");
        });
    }

    private Event getEvent(Long eventId) {
        log.info("getting event with id {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("getting event failure");
            return new NotFoundException("Event id =" + eventId + "is not found");
        });
    }

    private User getUser(Long userId) {
        log.info("getting user with id {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("validation failure");
            return new NotFoundException("User id =" + userId + "is not found");
        });
    }
}
