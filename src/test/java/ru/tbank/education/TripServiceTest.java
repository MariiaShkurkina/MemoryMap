package ru.tbank.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.education.DTO.CreateTripRequest;
import ru.tbank.education.DTO.TripResponse;
import ru.tbank.education.DTO.UpdateTripRequest;
import ru.tbank.education.Entity.Trip;
import ru.tbank.education.Entity.User;
import ru.tbank.education.Exception.NotFoundException;
import ru.tbank.education.Repository.TripRepository;
import ru.tbank.education.Repository.UserRepository;
import ru.tbank.education.Service.TripService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripService Tests")
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripService tripService;

    private User testUser;
    private Trip testTrip;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("User@mail.ru")
                .build();

        testTrip = Trip.builder()
                .id(100L)
                .user(testUser)
                .title("Paris Trip")
                .country("France")
                .city("Paris")
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 6, 10))
                .build();
    }

    // getAllByUser

    @Test
    @DisplayName("getAllByUser() returns trips for existing user")
    void getAllByUser_ReturnsTrips() {
        when(tripRepository.findAllByUser_Id(1L)).thenReturn(List.of(testTrip));

        List<TripResponse> result = tripService.getAllByUser(1L);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        verify(tripRepository).findAllByUser_Id(1L);
    }

    @Test
    @DisplayName("getAllByUser() returns empty list when no trips")
    void getAllByUser_ReturnsEmptyList() {
        when(tripRepository.findAllByUser_Id(1L)).thenReturn(List.of());

        assertTrue(tripService.getAllByUser(1L).isEmpty());
    }

    // getById

    @Test
    @DisplayName("getById() returns trip when exists")
    void getById_ReturnsTrip() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));

        TripResponse result = tripService.getById(1L, 100L);

        assertEquals(100L, result.getId());
        assertEquals("Paris Trip", result.getTitle());
    }

    @Test
    @DisplayName("getById() throws NotFoundException when trip not found")
    void getById_ThrowsNotFound() {
        when(tripRepository.findByIdAndUser_Id(999L, 1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> tripService.getById(1L, 999L)
        );
        assertEquals("Trip not found: 999", ex.getMessage());
    }

    // create

    @Test
    @DisplayName("create() successfully creates trip")
    void create_CreatesTrip() {
        CreateTripRequest req = validCreateRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        TripResponse result = tripService.create(1L, req);

        assertEquals(100L, result.getId());
        verify(tripRepository).save(any(Trip.class));

        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).save(captor.capture());
        assertEquals("New Trip", captor.getValue().getTitle());
        assertEquals("Italy", captor.getValue().getCountry());
    }

    @Test
    @DisplayName("create() throws NotFoundException when user not found")
    void create_ThrowsUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> tripService.create(999L, validCreateRequest())
        );
        verify(tripRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() throws error for invalid dates (endDate < startDate)")
    void create_ThrowsInvalidDates() {
        CreateTripRequest req = new CreateTripRequest();
        req.setTitle("Trip");
        req.setStartDate(LocalDate.of(2024, 7, 10));
        req.setEndDate(LocalDate.of(2024, 7, 1)); // Некорректно

        assertThrows(
                IllegalArgumentException.class,
                () -> tripService.create(1L, req)
        );
        verify(userRepository, never()).findById(any());
    }

    // update

    @Test
    @DisplayName("update() successfully updates trip")
    void update_UpdatesTrip() {
        // Arrange
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip); // ← КЛЮЧЕВАЯ СТРОКА!

        UpdateTripRequest req = validUpdateRequest();
        req.setTitle("Updated Trip");
        req.setCountry("Germany");
        req.setCity("Berlin");
        req.setStartDate(LocalDate.of(2024, 8, 1));
        req.setEndDate(LocalDate.of(2024, 8, 15));

        // Act
        TripResponse result = tripService.update(1L, 100L, req);

        // Assert
        // 1. Проверяем ответ сервиса
        assertEquals(100L, result.getId());
        assertEquals("Updated Trip", result.getTitle());
        assertEquals("Germany", result.getCountry());
        assertEquals("Berlin", result.getCity());

        // 2. Проверяем, что в save() передан объект с обновлёнными данными
        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).save(captor.capture());

        Trip savedTrip = captor.getValue();
        assertEquals("Updated Trip", savedTrip.getTitle());
        assertEquals("Germany", savedTrip.getCountry());
        assertEquals(LocalDate.of(2024, 8, 15), savedTrip.getEndDate());

        // 3. Проверяем вызов поиска
        verify(tripRepository).findByIdAndUser_Id(100L, 1L);
    }


    // delete

    @Test
    @DisplayName("delete() successfully deletes trip")
    void delete_DeletesTrip() {
        when(tripRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(testTrip));

        tripService.delete(1L, 100L);

        verify(tripRepository).delete(testTrip);
    }

    @Test
    @DisplayName("delete() throws NotFoundException when trip not found")
    void delete_ThrowsNotFound() {
        when(tripRepository.findByIdAndUser_Id(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> tripService.delete(1L, 999L)
        );
        verify(tripRepository, never()).delete(any());
    }


    private CreateTripRequest validCreateRequest() {
        CreateTripRequest req = new CreateTripRequest();
        req.setTitle("New Trip");
        req.setCountry("Italy");
        req.setCity("Rome");
        req.setStartDate(LocalDate.of(2024, 7, 1));
        req.setEndDate(LocalDate.of(2024, 7, 10));
        return req;
    }

    private UpdateTripRequest validUpdateRequest() {
        UpdateTripRequest req = new UpdateTripRequest();
        req.setTitle("Updated Trip");
        req.setCountry("France");
        req.setCity("Paris");
        req.setStartDate(LocalDate.of(2024, 6, 1));
        req.setEndDate(LocalDate.of(2024, 6, 10));
        return req;
    }
}