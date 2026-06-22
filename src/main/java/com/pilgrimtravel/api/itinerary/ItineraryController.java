package com.pilgrimtravel.api.itinerary;

import com.pilgrimtravel.api.itinerary.Itinerary;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/itineraries")
@CrossOrigin(origins = "http://localhost:3000")
public class ItineraryController {

    private final ItineraryService service;

    public ItineraryController(ItineraryService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public List<Itinerary> getUserItineraries(@PathVariable Long userId) {
        return service.findAllByUser(userId);
    }

    @GetMapping("/{id}")
    public Itinerary getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Itinerary create(@Valid @RequestBody Itinerary itinerary) {
        return service.create(itinerary);
    }

    @PutMapping("/{id}")
    public Itinerary update(@PathVariable Long id, @RequestBody Itinerary itinerary) {
        return service.update(id, itinerary);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
