package com.pilgrimtravel.api.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Extends JpaRepository to interact with PostgreSQL
    Optional<User> findByEmail(String email);
}
