package com.pilgrimtravel.api.itinerary;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByUserId(Long userId);
}
