package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "view_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app")
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "hits")
    private Long hits;

    public StatsView(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
