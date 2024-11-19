package ru.yandex.practicum.comment.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.dto.UpdateAdminDto;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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

    public CommentDto getCommentByAdmin(Long commentId) {
        log.info("attempt to get comment by admin {}", commentId);
        Comment comment = getComment(commentId);
        return CommentMapper.fromCommentToDto(comment);
    }

    @Transactional
    public CommentDto approveCommentByAdmin(Long commentId, UpdateAdminDto dto) {
        log.info("attempt to update comment {} by admin", commentId);
        Comment comment = getComment(commentId);

        if (!comment.getState().equals(CommentState.PENDING)) {
            log.warn("update failure");
            throw new ConflictException("Admin cannot approve published or cancelled comments");
        }

        if (dto.getState().equals(CommentState.PUBLISH)) {
            comment.setPublished(LocalDateTime.now());
            comment.setState(CommentState.PUBLISHED);
        } else if (dto.getState().equals(CommentState.REJECT)) {
            comment.setState(CommentState.REJECTED);
        } else {
            log.warn("moderation failure");
            throw new ValidationException("Unexpected state");
        }

        comment = commentRepository.save(comment);
        log.info("updating success");
        return CommentMapper.fromCommentToDto(comment);
    }

    public List<CommentDto> getCommentsByAdmin(Long[] events, String[] states,
                                               LocalDateTime start, LocalDateTime end, Integer from,
                                               Integer size) {
        log.info("attempt to get comments from repo");
        List<Comment> allComments;
        List<Comment> finalList = new ArrayList<>();

        if (start == null && end == null) {
            log.info("getting all comments from repo");
            allComments = commentRepository.findAllButLimit(from, size);
        } else {
            if (start != null && end != null) {
                allComments = commentRepository.findAllButLimitAndTime(from, size, start, end);
            } else if (start != null) {
                allComments = commentRepository.findAllButLimitAndStart(from, size, start);
            } else {
                allComments = commentRepository.findAllButLimitAndEnd(from, size, end);
            }
        }

        if (events != null) {
            log.info("filter by events");
            for (Long id : events) {
                List<Comment> sublist = allComments.stream()
                        .filter(c -> Objects.equals(c.getEvent().getId(),id))
                        .toList();
                finalList.addAll(sublist);
            }
        }

        if (states != null) {
            log.info("filter by states");
            for (String state : states) {
                List<Comment> sublist = allComments.stream()
                        .filter(c -> Objects.equals(c.getState(),CommentState.valueOf(state)))
                        .toList();
                finalList.addAll(sublist);
            }
        }

        if (finalList.isEmpty()) {
            log.info("no special filters");
            finalList = allComments;
        }

        return new HashSet<>(finalList).stream()
                .map(CommentMapper ::fromCommentToDto)
                .toList();
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
