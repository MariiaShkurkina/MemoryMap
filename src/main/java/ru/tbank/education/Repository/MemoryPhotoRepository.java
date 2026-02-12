package ru.tbank.education.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.education.Entity.MemoryPhoto;

import java.util.List;
import java.util.Optional;

public interface MemoryPhotoRepository extends JpaRepository<MemoryPhoto, Long> {
    List<MemoryPhoto> findAllByMemory_Id(Long memoryId);

    Optional<MemoryPhoto> findByIdAndMemory_Id(Long photoId, Long memoryId);
}