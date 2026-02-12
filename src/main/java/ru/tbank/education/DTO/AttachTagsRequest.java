package ru.tbank.education.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AttachTagsRequest {
    @NotEmpty
    private List<Long> tagIds;
}

