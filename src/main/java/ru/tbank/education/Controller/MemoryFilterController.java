package ru.tbank.education.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.DTO.MemoryResponse;
import ru.tbank.education.Service.MemoryService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/memories")
public class MemoryFilterController {

    private final MemoryService memoryService;

    // GET /api/trips/4/memories/filter?tagIds=2&tagIds=4, фильтрация воспоминаний по тегам
    @GetMapping("/filter")
    public List<MemoryResponse> filterByTagsInTrip(Authentication auth,
                                                   @PathVariable Long tripId,
                                                   @RequestParam List<Long> tagIds) {
        Long userId = (Long) auth.getPrincipal();
        return memoryService.filterByTagsInTrip(userId, tripId, tagIds);
    }
}

