package ru.tbank.education.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTripRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String country;

    @NotBlank
    private String city;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String description;
    private String coverPhotoPath;
}
