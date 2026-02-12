package ru.tbank.education.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GeoPlaceResponse {
    private String displayName;
    private Double lat;
    private Double lon;
}