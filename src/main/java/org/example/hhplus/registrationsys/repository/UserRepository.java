package org.example.hhplus.registrationsys.repository;

import java.util.Optional;
import org.example.hhplus.registrationsys.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByUserId(String userId);


}