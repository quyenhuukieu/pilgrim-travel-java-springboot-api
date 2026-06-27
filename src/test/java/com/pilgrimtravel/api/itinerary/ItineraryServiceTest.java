package com.pilgrimtravel.api.itinerary;

import com.pilgrimtravel.api.auth.User;
import com.pilgrimtravel.api.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItineraryServiceTest {

    private UserRepository userRepo;
    private ItineraryRepository repo;
    private ItineraryService service;

    @BeforeEach
    void setUp() {
        repo = mock(ItineraryRepository.class);
        userRepo = mock(UserRepository.class);
        service = new ItineraryService(repo, userRepo);
    }

    @Test
    void create_shouldSaveNewItinerary() {

        // Arrange
        String email = "test@example.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        Itinerary itinerary = new Itinerary();
        itinerary.setDestination("Jerusalem");
        itinerary.setDate(LocalDate.of(2026, 12, 25));
        itinerary.setCompleted(false);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        when(repo.save(any(Itinerary.class))).thenAnswer(inv -> {
            Itinerary i = inv.getArgument(0);
            i.setId(10L);
            return i;
        });

        // Act
        Itinerary result = service.create(email, itinerary);

        // Assert (CAPTURE what was passed INTO repository)
        ArgumentCaptor<Itinerary> captor = ArgumentCaptor.forClass(Itinerary.class);
        verify(repo).save(captor.capture());

        Itinerary passed = captor.getValue();

        assertEquals(1L, passed.getUserId());
        assertEquals("Jerusalem", passed.getDestination());
        assertEquals(LocalDate.of(2026, 12, 25), passed.getDate());
        assertFalse(passed.getCompleted());

        // service result should include generated id
        assertEquals(10L, result.getId());
    }

    @Test
    void findById_shouldReturnItinerary() {
        String email = "test@example.com";

        User user = new User();
        user.setId(1L);

        Itinerary itinerary = new Itinerary(
                5L,
                1L,
                "Rome",
                LocalDate.of(2026, 10, 1),
                false
        );

        when(userRepo.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(repo.findById(5L)).thenReturn(Optional.of(itinerary));

        Itinerary result = service.findById(email, 5L);

        assertEquals(5L, result.getId());
        assertEquals("Rome", result.getDestination());
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        String email = "test@example.com";

        User user = new User();
        user.setId(2L);

        when(userRepo.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(repo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.findById(email,99L));

        assertEquals("Itinerary not found", ex.getMessage());
    }

    @Test
    void findById_whenDifferentUser_throwsException() {

        String email = "test@example.com";

        User user = new User();
        user.setId(2L);

        Itinerary itinerary = new Itinerary(
                5L,
                1L,
                "Rome",
                LocalDate.of(2026, 10, 1),
                false
        );

        when(userRepo.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(repo.findById(5L))
                .thenReturn(Optional.of(itinerary));

        assertThrows(
                RuntimeException.class,
                () -> service.findById(email, 5L)
        );
    }

    @Test
    void findAllByUser_shouldReturnUserItineraries() {

        String email = "test@example.com";

        User user = new User();
        user.setId(1L);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        when(repo.findByUserId(1L))
                .thenReturn(List.of(new Itinerary(), new Itinerary()));

        List<Itinerary> result = service.findAllByUser(email);

        assertEquals(2, result.size());
        verify(repo).findByUserId(1L);
    }

    @Test
    void update_shouldModifyExistingItinerary() {

        String email = "test@example.com";

        User user = new User();
        user.setId(1L);

        Itinerary existing = new Itinerary();
        existing.setId(3L);
        existing.setUserId(1L);
        existing.setDestination("Old");
        existing.setDate(LocalDate.of(2026, 1, 1));
        existing.setCompleted(false);

        Itinerary updated = new Itinerary();
        updated.setDestination("New");
        updated.setDate(LocalDate.of(2026, 2, 2));
        updated.setCompleted(true);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Itinerary.class))).thenAnswer(inv -> inv.getArgument(0));

        Itinerary result = service.update(email, 3L, updated);

        assertEquals("New", result.getDestination());
        assertEquals(LocalDate.of(2026, 2, 2), result.getDate());
        assertTrue(result.getCompleted());
        assertEquals(3L, result.getId());
    }

    @Test
    void delete_whenOwnerDeletes_success() {

        String email = "test@example.com";
        User user = new User();
        user.setId(1L);

        Itinerary itinerary = new Itinerary(
                99L,
                1L,
                "Rome",
                null,
                false
        );

        when(repo.findById(99L))
                .thenReturn(Optional.of(itinerary));
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        service.delete(email,99L);
        verify(repo).deleteById(99L);
    }

    @Test
    void delete_whenDifferentUser_throwsException() {
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);

        Itinerary itinerary = new Itinerary(
                99L,
                1L,
                "Rome",
                null,
                false
        );

        when(repo.findById(99L))
                .thenReturn(Optional.of(itinerary));
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(
                RuntimeException.class,
                () ->
                        service.delete(
                                "hacker@example.com",
                                99L
                        )
        );

        verify(repo, never()).delete(any());
    }

    @Test
    void delete_whenMissing_throwsException() {

        when(repo.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () ->
                        service.delete(
                                "test@example.com",
                                99L
                        )
        );

        verify(repo, never()).delete(any());
    }
}