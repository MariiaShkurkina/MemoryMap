package ru.tbank.education.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "memory_photos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MemoryPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // к какому месту относится
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", nullable = false)
    private Memory memory;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String filePath;

}
