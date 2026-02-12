package ru.tbank.education.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.education.Entity.Memory;
import ru.tbank.education.Entity.MemoryPhoto;
import ru.tbank.education.Entity.Trip;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.MemoryPhotoRepository;
import ru.tbank.education.Repository.MemoryRepository;
import ru.tbank.education.Repository.TripRepository;
import ru.tbank.education.DTO.AddMemoryPhotoRequest;
import ru.tbank.education.DTO.MemoryPhotoResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoryPhotoService {

    private final MemoryPhotoRepository memoryPhotoRepository;
    private final TripRepository tripRepository;
    private final MemoryRepository memoryRepository;

    @Transactional(readOnly = true)
    public List<MemoryPhotoResponse> list(Long userId, Long tripId, Long memoryId) {
        Memory memory = getOwnedMemory(userId, tripId, memoryId);

        return memoryPhotoRepository.findAllByMemory_Id(memory.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MemoryPhotoResponse add(Long userId, Long tripId, Long memoryId, AddMemoryPhotoRequest req) {
        Memory memory = getOwnedMemory(userId, tripId, memoryId);

        MemoryPhoto photo = new MemoryPhoto();
        photo.setMemory(memory);
        photo.setFilePath(req.getFilePath());

        MemoryPhoto saved = memoryPhotoRepository.save(photo);
        return toResponse(saved);
    }

    public void delete(Long userId, Long tripId, Long memoryId, Long photoId) {
        Memory memory = getOwnedMemory(userId, tripId, memoryId);

        MemoryPhoto photo = memoryPhotoRepository.findByIdAndMemory_Id(photoId, memory.getId())
                .orElseThrow(() -> new NotFoundException("Photo not found: " + photoId));

        memoryPhotoRepository.delete(photo);
    }

    private Memory getOwnedMemory(Long userId, Long tripId, Long memoryId) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        return memoryRepository.findByIdAndTrip_Id(memoryId, trip.getId())
                .orElseThrow(() -> new NotFoundException("Memory not found: " + memoryId));
    }

    private MemoryPhotoResponse toResponse(MemoryPhoto p) {
        MemoryPhotoResponse dto = new MemoryPhotoResponse();
        dto.setId(p.getId());
        dto.setMemoryId(p.getMemory().getId());
        dto.setFilePath(p.getFilePath());
        return dto;
    }
}

