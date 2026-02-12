package ru.tbank.education.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.DTO.CreateTripRequest;
import ru.tbank.education.DTO.TripResponse;
import ru.tbank.education.DTO.UpdateTripRequest;
import ru.tbank.education.Service.TripService;

import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    // GET /api/trips, получить все мои поездки
    @GetMapping
    public List<TripResponse> getMyTrips(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return tripService.getAllByUser(userId);
    }

    // GET /api/trips/{tripId}, получить конкретную свою поездку
    @GetMapping("/{tripId}")
    public TripResponse getTrip(Authentication auth,
                                @PathVariable Long tripId) {
        Long userId = (Long) auth.getPrincipal();
        return tripService.getById(userId, tripId);
    }

    // POST /api/trips, создать поездку
    @PostMapping
    public TripResponse createTrip(Authentication auth,
                                   @Valid @RequestBody CreateTripRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return tripService.create(userId, req);
    }

    // PUT /api/trips/{tripId}, обновить поездку
    @PutMapping("/{tripId}")
    public TripResponse updateTrip(Authentication auth,
                                   @PathVariable Long tripId,
                                   @Valid @RequestBody UpdateTripRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return tripService.update(userId, tripId, req);
    }

    // DELETE /api/trips/{tripId}, удалить поездку
    @DeleteMapping("/{tripId}")
    public void deleteTrip(Authentication auth,
                           @PathVariable Long tripId) {
        Long userId = (Long) auth.getPrincipal();
        tripService.delete(userId, tripId);
    }
}
