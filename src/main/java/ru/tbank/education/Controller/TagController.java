package ru.tbank.education.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.Service.TagService;
import ru.tbank.education.DTO.CreateTagRequest;
import ru.tbank.education.DTO.TagResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    // всем пользователям (для выбора)
    @GetMapping
    public List<TagResponse> getAll() {
        return tagService.getAll();
    }

    // создать тег, для админа
    @PostMapping
    public TagResponse create(@Valid @RequestBody CreateTagRequest req) {
        return tagService.create(req);
    }

    // удалить тег, для админа
    @DeleteMapping("/{tagId}")
    public void delete(@PathVariable Long tagId) {
        tagService.delete(tagId);
    }
}

