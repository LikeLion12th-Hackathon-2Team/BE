package org.example.lionhackaton.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.example.lionhackaton.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
	List<Diary> findByUserIdAndIsFavoriteTrue(Long userId);

	List<Diary> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

	List<Diary> findByIsShared(Boolean isShared);

	@Query("SELECT d FROM Diary d WHERE d.isRepresentative = true AND d.diaryDate BETWEEN :startOfDay AND :endOfDay")
	List<Diary> findAllByIsRepresentativeTrueAndDiaryDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

	@Modifying
	@Transactional
	@Query("UPDATE Diary d SET d.isRepresentative = false WHERE d.diaryDate BETWEEN :startOfDay AND :endOfDay AND d.diaryId != :excludeId")
	void updateIsRepresentativeFalseByDiaryDateBetweenAndExcludeId(LocalDateTime startOfDay, LocalDateTime endOfDay, Long excludeId);

	List<Diary> findByUserIdAndDiaryDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}
