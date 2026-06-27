package com.pilgrimtravel.api.itinerary;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

    // GET all itineraries for logged-in user
    @GetMapping("/user")
    public List<Itinerary> getUserItineraries(Authentication authentication) {
        System.out.println("HIT CONTROLLER");
        String email = authentication.getName();
        System.out.println(
                "Controller authentication = " +
                        authentication.getName()
        );
        return service.findAllByUser(email);
    }

    // GET by id
    @GetMapping("/{id}")
    public Itinerary getById(Authentication authentication,
                             @PathVariable Long id) {
        String email = authentication.getName();
        return service.findById(email, id);
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Itinerary create(Authentication authentication,
                            @Valid @RequestBody Itinerary itinerary) {
        String email = authentication.getName();
        return service.create(email, itinerary);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Itinerary update(Authentication authentication,
                            @PathVariable Long id,
                            @Valid @RequestBody Itinerary itinerary) {
        String email = authentication.getName();
        return service.update(email, id, itinerary);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication,
                       @PathVariable Long id) {
        String email = authentication.getName();
        service.delete(email, id);
    }
}