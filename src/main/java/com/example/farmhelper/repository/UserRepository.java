package com.example.farmhelper.repository;

import com.example.farmhelper.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndIsActive(String username, Boolean isActive);

    List<User> findAllByUsernameContainingIgnoreCaseAndIsActive(String search, Boolean isActive);

    List<User> findAllByIsActive(Boolean isActive);
}
