package ru.tbank.education.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemoryResponse {
    private Long id;
    private Long tripId;
    private String title;
    private String note;
    private LocalDateTime visitedAt;
    private Double lat;
    private Double lon;
    private String addressLabel;
    private Integer rating;
}
