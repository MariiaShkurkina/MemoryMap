package ru.tbank.education.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.education.Entity.MemoryPhoto;

import java.util.List;

public interface MemoryPhotoRepository extends JpaRepository<MemoryPhoto, Long> {
    List<MemoryPhoto> findAllByMemoryId(Long memoryId);
}