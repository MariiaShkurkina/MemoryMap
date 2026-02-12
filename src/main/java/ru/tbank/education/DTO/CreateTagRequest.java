package ru.tbank.education.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTagRequest {
    @NotBlank
    private String name;
}
