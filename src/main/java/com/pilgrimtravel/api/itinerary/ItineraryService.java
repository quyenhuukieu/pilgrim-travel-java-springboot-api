package com.pilgrimtravel.api.itinerary;

import com.pilgrimtravel.api.auth.User;
import com.pilgrimtravel.api.auth.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItineraryService {

        private final ItineraryRepository itineraryRepo;
        private final UserRepository userRepo;

        public ItineraryService(ItineraryRepository itineraryRepo,
                                UserRepository userRepo) {

            this.itineraryRepo = itineraryRepo;
            this.userRepo = userRepo;
        }

        public List<Itinerary> findAllByUser(String email) {
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return itineraryRepo.findByUserId(user.getId());
        }

        public Itinerary findById(String email, Long id) {
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Itinerary itinerary = itineraryRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Itinerary not found"));

            if (!itinerary.getUserId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized");
            }

            return itinerary;
        }

        public Itinerary create(String email, Itinerary itinerary) {
            User user = userRepo.findByEmail(email)
                    .orElseThrow();

            itinerary.setUserId(user.getId());
            return itineraryRepo.save(itinerary);
        }

        public Itinerary update(String email, Long id, Itinerary updated) {
            User user = userRepo.findByEmail(email)
                    .orElseThrow();

            Itinerary existing = itineraryRepo.findById(id)
                    .orElseThrow();

            if (!existing.getUserId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized");
            }
            existing.setUserId(user.getId());
            existing.setDestination(updated.getDestination());
            existing.setDate(updated.getDate());
            existing.setCompleted(updated.getCompleted());
            return itineraryRepo.save(existing);
        }

        public void delete(String email, Long id) {
            User user = userRepo.findByEmail(email)
                    .orElseThrow();

            Itinerary existing = itineraryRepo.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Itinerary not found"));

            if (!existing.getUserId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized");
            }

            itineraryRepo.deleteById(id);
        }

}
