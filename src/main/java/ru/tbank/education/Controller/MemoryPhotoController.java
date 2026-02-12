package ru.tbank.education.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.Service.MemoryPhotoService;
import ru.tbank.education.DTO.AddMemoryPhotoRequest;
import ru.tbank.education.DTO.MemoryPhotoResponse;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/memories/{memoryId}/photos")
public class MemoryPhotoController {

    private final MemoryPhotoService memoryPhotoService;

    // GET /api/trips/{tripId}/memories/{memoryId}/photos
    @GetMapping
    public List<MemoryPhotoResponse> list(Authentication auth,
                                          @PathVariable Long tripId,
                                          @PathVariable Long memoryId) {
        Long userId = (Long) auth.getPrincipal();
        return memoryPhotoService.list(userId, tripId, memoryId);
    }

    // POST /api/trips/{tripId}/memories/{memoryId}/photos
    @PostMapping
    public MemoryPhotoResponse add(Authentication auth,
                                   @PathVariable Long tripId,
                                   @PathVariable Long memoryId,
                                   @Valid @RequestBody AddMemoryPhotoRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return memoryPhotoService.add(userId, tripId, memoryId, req);
    }

    // DELETE /api/trips/{tripId}/memories/{memoryId}/photos/{photoId}
    @DeleteMapping("/{photoId}")
    public void delete(Authentication auth,
                       @PathVariable Long tripId,
                       @PathVariable Long memoryId,
                       @PathVariable Long photoId) {
        Long userId = (Long) auth.getPrincipal();
        memoryPhotoService.delete(userId, tripId, memoryId, photoId);
    }
}
