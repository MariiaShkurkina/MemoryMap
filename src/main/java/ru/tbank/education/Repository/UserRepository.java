package ru.tbank.education.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.education.Entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
