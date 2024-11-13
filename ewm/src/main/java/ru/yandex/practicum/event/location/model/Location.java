package ru.yandex.practicum.event.location.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lat")
    private Long lat;
    @Column(name = "lon")
    private Long lon;

    public Location(Long lat, Long lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
