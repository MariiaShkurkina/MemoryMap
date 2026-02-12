package ru.tbank.education.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.Service.MemoryTagService;
import ru.tbank.education.DTO.AttachTagsRequest;
import ru.tbank.education.DTO.TagResponse;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/memories/{memoryId}/tags")
public class MemoryTagController {

    private final MemoryTagService memoryTagService;

    // GET /api/trips/{tripId}/memories/{memoryId}/tags, получить список всех тегов конкретного воспоминания
    @GetMapping
    public List<TagResponse> list(Authentication auth,
                                  @PathVariable Long tripId,
                                  @PathVariable Long memoryId) {
        Long userId = (Long) auth.getPrincipal();
        return memoryTagService.list(userId, tripId, memoryId);
    }

    // POST /api/trips/{tripId}/memories/{memoryId}/tags, привязать тег к воспоминанию
    @PostMapping
    public List<TagResponse> attach(Authentication auth,
                                    @PathVariable Long tripId,
                                    @PathVariable Long memoryId,
                                    @Valid @RequestBody AttachTagsRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return memoryTagService.attach(userId, tripId, memoryId, req);
    }

    // DELETE /api/trips/{tripId}/memories/{memoryId}/tags/{tagId}, отвязать тег от воспоминания
    @DeleteMapping("/{tagId}")
    public void detach(Authentication auth,
                       @PathVariable Long tripId,
                       @PathVariable Long memoryId,
                       @PathVariable Long tagId) {
        Long userId = (Long) auth.getPrincipal();
        memoryTagService.detach(userId, tripId, memoryId, tagId);
    }
}