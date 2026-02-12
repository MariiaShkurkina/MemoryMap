package ru.tbank.education.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.education.DTO.CreateMemoryRequest;
import ru.tbank.education.DTO.MemoryResponse;
import ru.tbank.education.DTO.UpdateMemoryRequest;
import ru.tbank.education.Entity.Memory;
import ru.tbank.education.Entity.Trip;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.MemoryRepository;
import ru.tbank.education.Repository.TripRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public List<MemoryResponse> getAllByTrip(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        return memoryRepository.findAllByTrip_Id(trip.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemoryResponse getById(Long userId, Long tripId, Long memoryId) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        Memory m = memoryRepository.findByIdAndTrip_Id(memoryId, trip.getId())
                .orElseThrow(() -> new NotFoundException("Memory not found: " + memoryId));

        return toResponse(m);
    }

    public MemoryResponse create(Long userId, Long tripId, CreateMemoryRequest req) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        validateLatLon(req.getLat(), req.getLon());
        validateRating(req.getRating());

        Memory m = new Memory();
        m.setTrip(trip);
        m.setTitle(req.getTitle());
        m.setNote(req.getNote());
        m.setVisitedAt(req.getVisitedAt());
        m.setLat(req.getLat());
        m.setLon(req.getLon());
        m.setAddressLabel(req.getAddressLabel());
        m.setRating(req.getRating());

        return toResponse(memoryRepository.save(m));
    }

    public MemoryResponse update(Long userId, Long tripId, Long memoryId, UpdateMemoryRequest req) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        Memory m = memoryRepository.findByIdAndTrip_Id(memoryId, trip.getId())
                .orElseThrow(() -> new NotFoundException("Memory not found: " + memoryId));

        validateLatLon(req.getLat(), req.getLon());
        validateRating(req.getRating());

        m.setTitle(req.getTitle());
        m.setNote(req.getNote());
        m.setVisitedAt(req.getVisitedAt());
        m.setLat(req.getLat());
        m.setLon(req.getLon());
        m.setAddressLabel(req.getAddressLabel());
        m.setRating(req.getRating());

        return toResponse(memoryRepository.save(m));
    }

    public void delete(Long userId, Long tripId, Long memoryId) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        Memory m = memoryRepository.findByIdAndTrip_Id(memoryId, trip.getId())
                .orElseThrow(() -> new NotFoundException("Memory not found: " + memoryId));

        memoryRepository.delete(m);
    }

    @Transactional(readOnly = true)
    public List<MemoryResponse> filterByTagsInTrip(Long userId, Long tripId, List<Long> tagIds) {

        // 1) Проверяем, что поездка принадлежит пользователю
        tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        // 2) Если тегов нет — вернем все воспоминания этой поездки (удобно для UI)
        if (tagIds == null || tagIds.isEmpty()) {
            return memoryRepository.findAllByTrip_IdAndTrip_User_Id(tripId, userId)
                    .stream().map(this::toResponse).toList();
        }

        List<Long> unique = tagIds.stream().distinct().toList();

        return memoryRepository.findAllByUserIdTripIdAndAllTags(userId, tripId, unique, (long) unique.size())
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private void validateRating(Integer rating) {
        if (rating == null) return;
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("rating must be in [1..5]");
    }

    private void validateLatLon(Double lat, Double lon) {
        if (lat == null || lon == null) return;
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("lat must be in [-90..90]");
        if (lon < -180 || lon > 180) throw new IllegalArgumentException("lon must be in [-180..180]");
    }

    private MemoryResponse toResponse(Memory m) {
        MemoryResponse dto = new MemoryResponse();
        dto.setId(m.getId());
        dto.setTripId(m.getTrip().getId());
        dto.setTitle(m.getTitle());
        dto.setNote(m.getNote());
        dto.setVisitedAt(m.getVisitedAt());
        dto.setLat(m.getLat());
        dto.setLon(m.getLon());
        dto.setAddressLabel(m.getAddressLabel());
        dto.setRating(m.getRating());
        return dto;
    }
}
