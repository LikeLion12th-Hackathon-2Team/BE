package org.example.lionhackaton.repository;

import java.util.List;

import org.example.lionhackaton.domain.DonateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonateHistoryRepository extends JpaRepository<DonateHistory, Long> {
	List<DonateHistory>  findAllByUserId(Long userId);
}
