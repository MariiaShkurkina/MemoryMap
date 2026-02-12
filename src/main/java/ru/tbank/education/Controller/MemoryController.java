package ru.tbank.education.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.DTO.CreateMemoryRequest;
import ru.tbank.education.DTO.MemoryResponse;
import ru.tbank.education.DTO.UpdateMemoryRequest;
import ru.tbank.education.Service.MemoryService;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/memories")
public class MemoryController {

    private final MemoryService memoryService;

    // GET /api/trips/{tripId}/memories, получить воспоминания поездки
    @GetMapping
    public List<MemoryResponse> list(Authentication auth,
                                     @PathVariable Long tripId) {
        Long userId = (Long) auth.getPrincipal();
        return memoryService.getAllByTrip(userId, tripId);
    }

    // GET /api/trips/{tripId}/memories/{memoryId}, получить конкретное воспоминание
    @GetMapping("/{memoryId}")
    public MemoryResponse get(Authentication auth,
                              @PathVariable Long tripId,
                              @PathVariable Long memoryId) {
        Long userId = (Long) auth.getPrincipal();
        return memoryService.getById(userId, tripId, memoryId);
    }

    // POST /api/trips/{tripId}/memories, создать воспоминание
    @PostMapping
    public MemoryResponse create(Authentication auth,
                                 @PathVariable Long tripId,
                                 @Valid @RequestBody CreateMemoryRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return memoryService.create(userId, tripId, req);
    }

    // PUT /api/trips/{tripId}/memories/{memoryId}, обновить воспоминание
    @PutMapping("/{memoryId}")
    public MemoryResponse update(Authentication auth,
                                 @PathVariable Long tripId,
                                 @PathVariable Long memoryId,
                                 @Valid @RequestBody UpdateMemoryRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return memoryService.update(userId, tripId, memoryId, req);
    }

    // DELETE /api/trips/{tripId}/memories/{memoryId}, удалить воспоминание
    @DeleteMapping("/{memoryId}")
    public void delete(Authentication auth,
                       @PathVariable Long tripId,
                       @PathVariable Long memoryId) {
        Long userId = (Long) auth.getPrincipal();
        memoryService.delete(userId, tripId, memoryId);
    }
}

