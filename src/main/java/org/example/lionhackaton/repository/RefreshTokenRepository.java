package org.example.lionhackaton.repository;

import java.util.Optional;

import org.example.lionhackaton.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUserId(Long userId);

	void deleteByUserId(Long userId);
}
