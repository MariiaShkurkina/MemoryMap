package ru.tbank.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.education.DTO.CreateTagRequest;
import ru.tbank.education.DTO.TagResponse;
import ru.tbank.education.Entity.Tag;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.MemoryRepository;
import ru.tbank.education.Repository.TagRepository;
import ru.tbank.education.Service.TagService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagService Tests")
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MemoryRepository memoryRepository;

    @InjectMocks
    private TagService tagService;

    private Tag testTag;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        testTag = Tag.builder()
                .id(1L)
                .name("java")
                .build();
    }

    // ТЕСТЫ ДЛЯ МЕТОДА getAll()
    @Test
    @DisplayName("getAll() should return list of tags when repository has tags")
    void getAll_ShouldReturnTags_WhenRepositoryHasTags() {
        // Arrange
        List<Tag> tags = List.of(
                Tag.builder().id(1L).name("java").build(),
                Tag.builder().id(2L).name("spring").build(),
                Tag.builder().id(3L).name("testing").build()
        );

        when(tagRepository.findAll()).thenReturn(tags);

        // Act
        List<TagResponse> result = tagService.getAll();

        // Assert
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("java", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("spring", result.get(1).getName());
        assertEquals(3L, result.get(2).getId());
        assertEquals("testing", result.get(2).getName());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll() should return empty list when repository is empty")
    void getAll_ShouldReturnEmptyList_WhenRepositoryIsEmpty() {
        // Arrange
        when(tagRepository.findAll()).thenReturn(List.of());

        // Act
        List<TagResponse> result = tagService.getAll();

        // Assert
        assertTrue(result.isEmpty());
        verify(tagRepository, times(1)).findAll();
    }

    // ТЕСТЫ ДЛЯ МЕТОДА create()
    @Test
    @DisplayName("create() should create new tag when tag doesn't exist")
    void create_ShouldCreateNewTag_WhenTagDoesNotExist() {
        // Arrange
        CreateTagRequest request = new CreateTagRequest();
        request.setName("newTag");

        Tag createdTag = Tag.builder()
                .id(100L)
                .name("newTag")
                .build();

        when(tagRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(createdTag);

        // Act
        TagResponse result = tagService.create(request);

        // Assert
        assertEquals(100L, result.getId());
        assertEquals("newTag", result.getName()); // Регистр сохраняется как после trim()

        // Проверяем вызовы
        verify(tagRepository).findByNameIgnoreCase(anyString());
        verify(tagRepository).save(any(Tag.class));

        // Дополнительная проверка аргумента (игнорируем регистр при сравнении)
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(tagRepository).findByNameIgnoreCase(nameCaptor.capture());
        assertEquals("newtag", nameCaptor.getValue().toLowerCase()); // Нормализуем для проверки
    }

    @Test
    @DisplayName("create() should return existing tag when tag already exists (case insensitive)")
    void create_ShouldReturnExistingTag_WhenTagAlreadyExists() {
        // Arrange
        CreateTagRequest request = new CreateTagRequest();
        request.setName("JAVA"); // После trim() → "JAVA"


        when(tagRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(testTag));

        // Act
        TagResponse result = tagService.create(request);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("java", result.getName()); // Возвращается существующий тег с его оригинальным именем

        // Проверяем, что save НЕ вызывался
        verify(tagRepository).findByNameIgnoreCase(anyString());
        verify(tagRepository, never()).save(any());

        // Дополнительная проверка аргумента (игнорируем регистр)
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(tagRepository).findByNameIgnoreCase(nameCaptor.capture());
        assertEquals("java", nameCaptor.getValue().toLowerCase());
    }

    @Test
    @DisplayName("create() should throw IllegalArgumentException when tag name is blank")
    void create_ShouldThrowException_WhenTagNameIsBlank() {
        // Arrange
        CreateTagRequest request = new CreateTagRequest();
        request.setName("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tagService.create(request)
        );

        assertEquals("Tag name is blank", exception.getMessage());
        verify(tagRepository, never()).findByNameIgnoreCase(any());
        verify(tagRepository, never()).save(any());
    }

    // ТЕСТЫ ДЛЯ МЕТОДА delete()
    @Test
    @DisplayName("delete() should delete tag when tag exists")
    void delete_ShouldDeleteTag_WhenTagExists() {
        // Arrange
        Long tagId = 1L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

        // Act
        tagService.delete(tagId);

        // Assert
        InOrder inOrder = inOrder(memoryRepository, tagRepository);
        inOrder.verify(memoryRepository).detachTagEverywhere(tagId);
        inOrder.verify(tagRepository).delete(testTag);

        verify(memoryRepository, times(1)).detachTagEverywhere(tagId);
        verify(tagRepository, times(1)).delete(testTag);
    }

    @Test
    @DisplayName("delete() should throw NotFoundException when tag doesn't exist")
    void delete_ShouldThrowException_WhenTagDoesNotExist() {
        // Arrange
        Long tagId = 999L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> tagService.delete(tagId)
        );

        assertEquals("Tag not found: 999", exception.getMessage());
        verify(memoryRepository, never()).detachTagEverywhere(any());
        verify(tagRepository, never()).delete(any());
    }

    @Test
    @DisplayName("delete() should detach tag from memories before deleting")
    void delete_ShouldDetachTagBeforeDeleting() {
        // Arrange
        Long tagId = 1L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

        // Act
        tagService.delete(tagId);

        // Assert
        InOrder inOrder = inOrder(memoryRepository, tagRepository);
        inOrder.verify(memoryRepository).detachTagEverywhere(tagId);
        inOrder.verify(tagRepository).delete(testTag);
    }
}