package ru.tbank.education.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.education.Entity.Trip;
import ru.tbank.education.DTO.CreateTripRequest;
import ru.tbank.education.DTO.TripResponse;
import ru.tbank.education.DTO.UpdateTripRequest;
import ru.tbank.education.Entity.User;
import ru.tbank.education.Repository.UserRepository;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.TripRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    //Вернуть поездки только этого пользователя
    @Transactional(readOnly = true)
    public List<TripResponse> getAllByUser(Long userId) {
        return tripRepository.findAllByUser_Id(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //Вернуть поездку по id
    @Transactional(readOnly = true)
    public TripResponse getById(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));
        return toResponse(trip);
    }

    //Создать поездку
    public TripResponse create(Long userId, CreateTripRequest req) {
        validateDates(req.getStartDate(), req.getEndDate());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTitle(req.getTitle());
        trip.setCountry(req.getCountry());
        trip.setCity(req.getCity());
        trip.setStartDate(req.getStartDate());
        trip.setEndDate(req.getEndDate());
        trip.setDescription(req.getDescription());
        trip.setCoverPhotoPath(req.getCoverPhotoPath());

        return toResponse(tripRepository.save(trip));
    }
    //Обновить поездку
    public TripResponse update(Long userId, Long tripId, UpdateTripRequest req) {//Обновить информацию о поездке
        validateDates(req.getStartDate(), req.getEndDate());

        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        trip.setTitle(req.getTitle());
        trip.setCountry(req.getCountry());
        trip.setCity(req.getCity());
        trip.setStartDate(req.getStartDate());
        trip.setEndDate(req.getEndDate());
        trip.setDescription(req.getDescription());
        trip.setCoverPhotoPath(req.getCoverPhotoPath());

        Trip saved = tripRepository.save(trip);
        return toResponse(saved);
    }

    //Удалить поездку
    public void delete(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));
        tripRepository.delete(trip);
    }
    //Проверка дат
    private void validateDates(java.time.LocalDate start, java.time.LocalDate end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endDate must be >= startDate");
        }
    }

    private TripResponse toResponse(Trip t) {
        TripResponse dto = new TripResponse();

        dto.setId(t.getId());
        dto.setUserId(t.getUser().getId());

        dto.setTitle(t.getTitle());
        dto.setCountry(t.getCountry());
        dto.setCity(t.getCity());

        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());

        dto.setDescription(t.getDescription());
        dto.setCoverPhotoPath(t.getCoverPhotoPath());

        return dto;
    }
}
