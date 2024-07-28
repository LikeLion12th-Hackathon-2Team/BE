package org.example.lionhackaton.repository;

import java.util.Optional;

import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.oauth.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
