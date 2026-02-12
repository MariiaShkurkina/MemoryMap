package ru.tbank.education.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.education.DTO.AttachTagsRequest;
import ru.tbank.education.DTO.TagResponse;
import ru.tbank.education.Entity.Memory;
import ru.tbank.education.Entity.Tag;
import ru.tbank.education.Entity.Trip;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.MemoryRepository;
import ru.tbank.education.Repository.TagRepository;
import ru.tbank.education.Repository.TripRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoryTagService {

    private final TripRepository tripRepository;
    private final MemoryRepository memoryRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true) //получить все теги воспоминания
    public List<TagResponse> list(Long userId, Long tripId, Long memoryId) {
        Memory memory = getOwnedMemory(userId, tripId, memoryId);
        return memory.getTags().stream().map(this::toResponse).toList();
    }
//привязать теги к воспоминанию
    public List<TagResponse> attach(Long userId, Long tripId, Long memoryId, AttachTagsRequest req) {
        Memory memory = getOwnedMemory(userId, tripId, memoryId);

        for (Long tagId : req.getTagIds()) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new NotFoundException("Tag not found: " + tagId));
            memory.getTags().add(tag);
        }

        memoryRepository.save(memory);
        return memory.getTags().stream().map(this::toResponse).toList();
    }
//отвязать теги от воспоминания
    public void detach(Long userId, Long tripId, Long memoryId, Long tagId) {
        Memory memory = getOwnedMemory(userId, tripId, memoryId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag not found: " + tagId));

        memory.getTags().remove(tag);
        memoryRepository.save(memory);
    }

    private Memory getOwnedMemory(Long userId, Long tripId, Long memoryId) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        return memoryRepository.findByIdAndTrip_Id(memoryId, trip.getId())
                .orElseThrow(() -> new NotFoundException("Memory not found: " + memoryId));
    }

    private TagResponse toResponse(Tag t) {
        TagResponse dto = new TagResponse();
        dto.setId(t.getId());
        dto.setName(t.getName());
        return dto;
    }
}
