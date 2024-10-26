package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "view_stats")
@Getter
@Setter
@NoArgsConstructor
public class StatsView {
    @Column(name = "app")
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "hits")
    private Long hits;
}
