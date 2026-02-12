package ru.tbank.education.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "memories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // к какой поездке относится маркер
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false, length = 140)
    private String title;

    // твоя заметка/воспоминание
    @Column(columnDefinition = "TEXT")
    private String note;

    // дата/время посещения
    private LocalDateTime visitedAt;

    // координаты для карты
    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    // что ввели/нашли ("Tiergarten Berlin")
    @Column(name = "address_label", length = 220)
    private String addressLabel;

    // оценка 1..5
    private Integer rating;

    // фотки места
    @OneToMany(mappedBy = "memory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemoryPhoto> photos = new ArrayList<>();

    // теги (many-to-many через join table)
    @ManyToMany
    @JoinTable(
            name = "memory_tags",
            joinColumns = @JoinColumn(name = "memory_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

}
