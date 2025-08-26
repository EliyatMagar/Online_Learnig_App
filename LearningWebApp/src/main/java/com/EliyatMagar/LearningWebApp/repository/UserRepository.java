package com.EliyatMagar.LearningWebApp.repository;

import com.EliyatMagar.LearningWebApp.model.Role;
import com.EliyatMagar.LearningWebApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRole(Role role); // Useful for finding all instructors or students
}
