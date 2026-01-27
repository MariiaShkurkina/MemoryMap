package ru.tbank.education.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.education.Entity.Trip;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findAllByUser_Id(Long userId);
    Optional<Trip> findByIdAndUser_Id(Long id, Long userId);
}