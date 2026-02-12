package ru.tbank.education.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateMemoryRequest {
    @NotBlank
    private String title;
    private String note;
    private LocalDateTime visitedAt;
    @NotNull
    private Double lat;
    @NotNull private Double lon;
    private String addressLabel;
    private Integer rating;
}
