package com.pilgrimtravel.api.itinerary;

import com.pilgrimtravel.api.itinerary.Itinerary;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItineraryService {

        private final ItineraryRepository repo;

        public ItineraryService(ItineraryRepository repo) {
            this.repo = repo;
        }

        public List<Itinerary> findAllByUser(Long userId) {
            return repo.findByUserId(userId);
        }

        public Itinerary findById(Long id) {
            return repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Itinerary not found"));
        }

        public Itinerary create(Itinerary itinerary) {
            itinerary.setId(null);
            return repo.save(itinerary);
        }

        public Itinerary update(Long id, Itinerary updated) {
            Itinerary existing = findById(id);
            existing.setDestination(updated.getDestination());
            existing.setDate(updated.getDate());
            existing.setCompleted(updated.getCompleted());
            return repo.save(existing);
        }

        public void delete(Long id) {
            repo.deleteById(id);
        }

}
