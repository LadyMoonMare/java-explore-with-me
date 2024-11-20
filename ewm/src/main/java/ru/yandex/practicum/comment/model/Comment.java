package ru.yandex.practicum.comment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "author_id")
    private Long author;
    @Column(name = "description")
    private String description;
    @Column(name = "state")
    private CommentState state;
    @Column(name = "created_on")
    private LocalDateTime created;
    @Column(name = "published_on")
    private LocalDateTime published;
    @Column(name = "event_id")
    private Long event;

    public Comment(Long author, String description, CommentState state, LocalDateTime created, Long event) {
        this.author = author;
        this.description = description;
        this.state = state;
        this.created = created;
        this.event = event;
    }
}