package ru.tbank.education.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.education.Entity.Tag;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.MemoryRepository;
import ru.tbank.education.Repository.TagRepository;
import ru.tbank.education.DTO.CreateTagRequest;
import ru.tbank.education.DTO.TagResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final MemoryRepository memoryRepository;

    //получить все теги
    @Transactional(readOnly = true)
    public List<TagResponse> getAll() {
        return tagRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    //создать тег
    public TagResponse create(CreateTagRequest req) {
        String name = req.getName().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Tag name is blank");
        }

        // если такой тег уже существует (без учета регистра) — возвращаем его
        Tag tag = tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));

        return toResponse(tag);
    }
    //удалить тег
    public void delete(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag not found: " + tagId));

        // 1) сперва чистим связи many-to-many (join table)
        memoryRepository.detachTagEverywhere(tagId);

        // 2) потом удаляем сам тег
        tagRepository.delete(tag);
    }
    private TagResponse toResponse(Tag t) {
        TagResponse dto = new TagResponse();
        dto.setId(t.getId());
        dto.setName(t.getName());
        return dto;
    }
}
