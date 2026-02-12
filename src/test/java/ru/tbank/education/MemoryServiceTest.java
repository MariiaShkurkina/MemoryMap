package ru.tbank.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.education.DTO.CreateMemoryRequest;
import ru.tbank.education.DTO.MemoryResponse;
import ru.tbank.education.DTO.UpdateMemoryRequest;
import ru.tbank.education.Entity.Memory;
import ru.tbank.education.Entity.Trip;
import ru.tbank.education.Entity.User;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.MemoryRepository;
import ru.tbank.education.Repository.TripRepository;
import ru.tbank.education.Service.MemoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemoryService Tests")
class MemoryServiceTest {

    @Mock
    private MemoryRepository memoryRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private MemoryService memoryService;

    private Trip testTrip;
    private Memory testMemory;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("User@mail.ru").build();

        testTrip = Trip.builder()
                .id(100L)
                .user(user)
                .title("Paris Trip")
                .build();

        testMemory = Memory.builder()
                .id(200L)
                .trip(testTrip)
                .title("Eiffel Tower")
                .note("Amazing view")
                .visitedAt(LocalDateTime.of(2024, 6, 5,12, 50, 30))
                .lat(48.8584)
                .lon(2.2945)
                .addressLabel("Paris, France")
                .rating(5)
                .build();
    }

    // getAllByTrip

    @Test
    @DisplayName("getAllByTrip() returns memories for existing trip")
    void getAllByTrip_ReturnsMemories() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findAllByTrip_Id(100L)).thenReturn(List.of(testMemory));

        List<MemoryResponse> result = memoryService.getAllByTrip(1L, 100L);

        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getId());
        assertEquals("Eiffel Tower", result.get(0).getTitle());
        verify(tripRepository).findByIdAndUser_Id(100L, 1L);
        verify(memoryRepository).findAllByTrip_Id(100L);
    }

    @Test
    @DisplayName("getAllByTrip() throws NotFoundException when trip not found")
    void getAllByTrip_ThrowsNotFound() {
        when(tripRepository.findByIdAndUser_Id(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> memoryService.getAllByTrip(1L, 999L)
        );
        verify(memoryRepository, never()).findAllByTrip_Id(any());
    }

    // getById

    @Test
    @DisplayName("getById() returns memory when exists")
    void getById_ReturnsMemory() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findByIdAndTrip_Id(200L, 100L)).thenReturn(Optional.of(testMemory));

        MemoryResponse result = memoryService.getById(1L, 100L, 200L);

        assertEquals(200L, result.getId());
        assertEquals("Eiffel Tower", result.getTitle());
        assertEquals(5, result.getRating());
        verify(memoryRepository).findByIdAndTrip_Id(200L, 100L);
    }

    // create

    @Test
    @DisplayName("create() successfully creates memory")
    void create_CreatesMemory() {
        // Arrange
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        // Возвращаем НОВЫЙ объект с данными из запроса
        Memory savedMemory = Memory.builder()
                .id(200L)
                .trip(testTrip)
                .title("Louvre Museum")
                .note("World's largest art museum")
                .visitedAt(LocalDateTime.of(2024, 6, 3, 12, 23, 44))
                .lat(48.8606)
                .lon(2.3376)
                .addressLabel("Paris, France")
                .rating(4)
                .build();
        when(memoryRepository.save(any(Memory.class))).thenReturn(savedMemory);

        CreateMemoryRequest req = validCreateRequest();

        // Act
        MemoryResponse result = memoryService.create(1L, 100L, req);

        // Assert
        assertEquals(200L, result.getId());
        assertEquals("Louvre Museum", result.getTitle());
        assertEquals(4, result.getRating());

        // Проверяем данные, переданные в save()
        ArgumentCaptor<Memory> captor = ArgumentCaptor.forClass(Memory.class);
        verify(memoryRepository).save(captor.capture());

        Memory captured = captor.getValue();
        assertEquals(testTrip, captured.getTrip());
        assertEquals("Louvre Museum", captured.getTitle());
        assertEquals(48.8606, captured.getLat(), 0.0001); // ← КРИТИЧНО: дельта для double!
        assertEquals(2.3376, captured.getLon(), 0.0001);  // ← КРИТИЧНО: дельта для double!
        assertEquals(Integer.valueOf(4), captured.getRating());
    }
    @Test
    @DisplayName("create() throws NotFoundException when trip not found")
    void create_ThrowsTripNotFound() {
        when(tripRepository.findByIdAndUser_Id(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> memoryService.create(1L, 999L, validCreateRequest())
        );
        verify(memoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() throws error for invalid latitude")
    void create_ThrowsInvalidLat() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));

        CreateMemoryRequest req = validCreateRequest();
        req.setLat(100.0); // Некорректно (> 90)

        assertThrows(
                IllegalArgumentException.class,
                () -> memoryService.create(1L, 100L, req)
        );
        verify(memoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() throws error for invalid longitude")
    void create_ThrowsInvalidLon() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));

        CreateMemoryRequest req = validCreateRequest();
        req.setLon(200.0); // Некорректно (> 180)

        assertThrows(
                IllegalArgumentException.class,
                () -> memoryService.create(1L, 100L, req)
        );
        verify(memoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() throws error for invalid rating")
    void create_ThrowsInvalidRating() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));

        CreateMemoryRequest req = validCreateRequest();
        req.setRating(6); // Некорректно (> 5)

        assertThrows(
                IllegalArgumentException.class,
                () -> memoryService.create(1L, 100L, req)
        );
        verify(memoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() allows null rating and coordinates")
    void create_AllowsNullFields() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.save(any(Memory.class))).thenReturn(testMemory);

        CreateMemoryRequest req = new CreateMemoryRequest();
        req.setTitle("Memory without details");
        req.setNote("No rating, no coordinates");
        req.setVisitedAt(LocalDateTime.of(2024, 6, 1, 10, 45, 18));
        // lat, lon, rating = null

        MemoryResponse result = memoryService.create(1L, 100L, req);

        assertNotNull(result);
        verify(memoryRepository).save(any(Memory.class));
    }

    // update

    @Test
    @DisplayName("update() successfully updates memory")
    void update_UpdatesMemory() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findByIdAndTrip_Id(200L, 100L)).thenReturn(Optional.of(testMemory));
        when(memoryRepository.save(any(Memory.class))).thenReturn(testMemory);

        UpdateMemoryRequest req = new UpdateMemoryRequest();
        req.setTitle("Updated Eiffel Tower");
        req.setNote("Even better view");
        req.setVisitedAt(LocalDateTime.of(2024, 6, 6, 12, 33, 23));
        req.setLat(48.8585);
        req.setLon(2.2946);
        req.setAddressLabel("Updated address");
        req.setRating(4);

        MemoryResponse result = memoryService.update(1L, 100L, 200L, req);

        assertEquals("Updated Eiffel Tower", result.getTitle());
        assertEquals(4, result.getRating());

        assertEquals("Updated Eiffel Tower", testMemory.getTitle());
        assertEquals(48.8585, testMemory.getLat());
        assertEquals(4, testMemory.getRating());
        verify(memoryRepository).save(testMemory);
    }


    // delete

    @Test
    @DisplayName("delete() successfully deletes memory")
    void delete_DeletesMemory() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findByIdAndTrip_Id(200L, 100L)).thenReturn(Optional.of(testMemory));

        memoryService.delete(1L, 100L, 200L);

        verify(memoryRepository).delete(testMemory);
    }

    @Test
    @DisplayName("delete() throws NotFoundException when memory not found")
    void delete_ThrowsNotFound() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findByIdAndTrip_Id(999L, 100L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> memoryService.delete(1L, 100L, 999L)
        );
        verify(memoryRepository, never()).delete(any());
    }

    // filterByTagsInTrip

    @Test
    @DisplayName("filterByTagsInTrip() returns all memories when tagIds is empty")
    void filterByTagsInTrip_ReturnsAllWhenEmptyTags() {
        // Arrange
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findAllByTrip_IdAndTrip_User_Id(100L, 1L))
                .thenReturn(List.of(testMemory));

        // Act
        List<MemoryResponse> result = memoryService.filterByTagsInTrip(1L, 100L, List.of());

        // Assert
        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getId());
        assertEquals("Eiffel Tower", result.get(0).getTitle());

        // Проверяем правильные вызовы
        verify(tripRepository).findByIdAndUser_Id(100L, 1L);
        verify(memoryRepository).findAllByTrip_IdAndTrip_User_Id(100L, 1L);
        verify(memoryRepository, never()).findAllByUserIdTripIdAndAllTags(anyLong(), anyLong(), anyList(), anyLong());
    }

    @Test
    @DisplayName("filterByTagsInTrip() returns all memories when tagIds is null")
    void filterByTagsInTrip_ReturnsAllWhenNullTags() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findAllByTrip_IdAndTrip_User_Id(100L, 1L))
                .thenReturn(List.of(testMemory));

        List<MemoryResponse> result = memoryService.filterByTagsInTrip(1L, 100L, null);

        assertEquals(1, result.size());
        verify(memoryRepository).findAllByTrip_IdAndTrip_User_Id(100L, 1L);
    }

    @Test
    @DisplayName("filterByTagsInTrip() filters memories by tags")
    void filterByTagsInTrip_FiltersByTags() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        List<Memory> filtered = List.of(testMemory);
        when(memoryRepository.findAllByUserIdTripIdAndAllTags(1L, 100L, List.of(1L, 2L), 2L))
                .thenReturn(filtered);

        List<MemoryResponse> result = memoryService.filterByTagsInTrip(1L, 100L, List.of(1L, 2L));

        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getId());
        verify(memoryRepository).findAllByUserIdTripIdAndAllTags(1L, 100L, List.of(1L, 2L), 2L);
    }

    @Test
    @DisplayName("filterByTagsInTrip() removes duplicate tagIds")
    void filterByTagsInTrip_RemovesDuplicates() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(memoryRepository.findAllByUserIdTripIdAndAllTags(1L, 100L, List.of(1L, 2L), 2L))
                .thenReturn(List.of(testMemory));

        // Передаём дубликаты
        memoryService.filterByTagsInTrip(1L, 100L, List.of(1L, 2L, 1L, 2L));

        // Проверяем, что в репозиторий переданы уникальные значения
        verify(memoryRepository).findAllByUserIdTripIdAndAllTags(1L, 100L, List.of(1L, 2L), 2L);
    }


    private CreateMemoryRequest validCreateRequest() {
        CreateMemoryRequest req = new CreateMemoryRequest();
        req.setTitle("Louvre Museum");
        req.setNote("World's largest art museum");
        req.setVisitedAt(LocalDateTime.of(2024, 6, 3, 11, 56, 55));
        req.setLat(48.8606);
        req.setLon(2.3376);
        req.setAddressLabel("Paris, France");
        req.setRating(4);
        return req;
    }
}