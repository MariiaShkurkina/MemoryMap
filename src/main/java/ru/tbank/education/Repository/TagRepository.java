package ru.tbank.education.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.education.Entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);
}
