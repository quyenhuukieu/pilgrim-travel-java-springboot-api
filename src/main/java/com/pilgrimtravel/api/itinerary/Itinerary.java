package com.pilgrimtravel.api.itinerary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="itineraries")
@Getter @Setter
@NoArgsConstructor      // Required by JPA
@AllArgsConstructor     // Supports your test constructor
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId; // associate itinerary with logged-in user

    @NotBlank
    private String destination;

    @NotNull
    private LocalDate date;

    @NotNull
    private Boolean completed;
}
