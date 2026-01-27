package ru.tbank.education.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.education.Entity.Memory;

import java.util.List;
import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findAllByTripId(Long tripId);
    Optional<Memory> findByIdAndTripId(Long id, Long tripId);
}