package org.example.lionhackaton.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.lionhackaton.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
	List<Diary> findByUserIdAndIsFavoriteTrue(Long userId);

	List<Diary> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

	List<Diary> findByIsShared(Boolean isShared);
}
