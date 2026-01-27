package ru.tbank.education.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.DTO.CreateTripRequest;
import ru.tbank.education.DTO.TripResponse;
import ru.tbank.education.DTO.UpdateTripRequest;
import ru.tbank.education.Service.TripService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    // Список моих поездок (временно userId через query param)
    // GET /api/trips?userId=1
    @GetMapping
    public List<TripResponse> getMyTrips(@RequestParam Long userId) {
        return tripService.getAllByUser(userId);
    }

    // Получить одну поездку
    // GET /api/trips/{tripId}?userId=1
    @GetMapping("/{tripId}")
    public TripResponse getTrip(@RequestParam Long userId,
                                @PathVariable Long tripId) {
        return tripService.getById(userId, tripId);
    }

    // Создать поездку
    // POST /api/trips?userId=1
    @PostMapping
    public TripResponse createTrip(@RequestParam Long userId,
                                   @Valid @RequestBody CreateTripRequest req) {
        return tripService.create(userId, req);
    }

    // Обновить поездку
    // PUT /api/trips/{tripId}?userId=1
    @PutMapping("/{tripId}")
    public TripResponse updateTrip(@RequestParam Long userId,
                                   @PathVariable Long tripId,
                                   @Valid @RequestBody UpdateTripRequest req) {
        return tripService.update(userId, tripId, req);
    }

    // Удалить поездку
    // DELETE /api/trips/{tripId}?userId=1
    @DeleteMapping("/{tripId}")
    public void deleteTrip(@RequestParam Long userId,
                           @PathVariable Long tripId) {
        tripService.delete(userId, tripId);
    }
}
