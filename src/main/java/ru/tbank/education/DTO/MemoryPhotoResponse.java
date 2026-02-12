package ru.tbank.education.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoryPhotoResponse {
    private Long id;
    private Long memoryId;
    private String filePath;
}
