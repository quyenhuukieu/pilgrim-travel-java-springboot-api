package com.pilgrimtravel.api.itinerary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItineraryServiceTest {

    private ItineraryRepository repo;
    private ItineraryService service;

    @BeforeEach
    void setUp() {
        repo = mock(ItineraryRepository.class);
        service = new ItineraryService(repo);
    }

    @Test
    void create_shouldSaveNewItinerary() {
        Itinerary itinerary = new Itinerary();
        itinerary.setDestination("Jerusalem");
        itinerary.setDate(LocalDate.of(2026, 12, 25));
        itinerary.setCompleted(false);
        itinerary.setUserId(1L);

        Itinerary saved = new Itinerary();
        saved.setId(10L);
        saved.setDestination(itinerary.getDestination());
        saved.setDate(itinerary.getDate());
        saved.setCompleted(itinerary.getCompleted());
        saved.setUserId(itinerary.getUserId());

        when(repo.save(any(Itinerary.class))).thenReturn(saved);

        Itinerary result = service.create(itinerary);

        ArgumentCaptor<Itinerary> captor = ArgumentCaptor.forClass(Itinerary.class);
        verify(repo).save(captor.capture());

        Itinerary passed = captor.getValue();
        assertNull(passed.getId());
        assertEquals("Jerusalem", passed.getDestination());
        assertEquals(LocalDate.of(2026, 12, 25), passed.getDate());
        assertFalse(passed.getCompleted());
        assertEquals(1L, passed.getUserId());

        assertEquals(10L, result.getId());
    }

    @Test
    void findById_shouldReturnItinerary() {
        Itinerary itinerary = new Itinerary();
        itinerary.setId(5L);
        itinerary.setDestination("Rome");

        when(repo.findById(5L)).thenReturn(Optional.of(itinerary));

        Itinerary result = service.findById(5L);

        assertEquals(5L, result.getId());
        assertEquals("Rome", result.getDestination());
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.findById(99L));

        assertEquals("Itinerary not found", ex.getMessage());
    }

    @Test
    void findAllByUser_shouldDelegateToRepository() {
        when(repo.findByUserId(1L)).thenReturn(List.of(new Itinerary(), new Itinerary()));

        List<Itinerary> result = service.findAllByUser(1L);

        assertEquals(2, result.size());
        verify(repo).findByUserId(1L);
    }

    @Test
    void update_shouldModifyExistingItinerary() {
        Itinerary existing = new Itinerary();
        existing.setId(3L);
        existing.setDestination("Old");
        existing.setDate(LocalDate.of(2026, 1, 1));
        existing.setCompleted(false);

        Itinerary updated = new Itinerary();
        updated.setDestination("New");
        updated.setDate(LocalDate.of(2026, 2, 2));
        updated.setCompleted(true);

        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Itinerary.class))).thenAnswer(inv -> inv.getArgument(0));

        Itinerary result = service.update(3L, updated);

        assertEquals("New", result.getDestination());
        assertEquals(LocalDate.of(2026, 2, 2), result.getDate());
        assertTrue(result.getCompleted());
        assertEquals(3L, result.getId());
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        service.delete(7L);
        verify(repo).deleteById(7L);
    }
}
