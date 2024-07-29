package org.example.lionhackaton.repository;

import java.util.Optional;

import org.example.lionhackaton.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
