package org.example.lionhackaton.repository;

import java.util.Optional;

import org.example.lionhackaton.domain.TodayRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayRecommendRepository extends JpaRepository<TodayRecommend, Long> {
	Optional<TodayRecommend> findByUserId(Long userId);
}
