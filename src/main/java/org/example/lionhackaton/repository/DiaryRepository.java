package org.example.lionhackaton.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.example.lionhackaton.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM Diary d WHERE d.diaryId = :diaryId")
	void deleteByDiaryId(@Param("diaryId") Long diaryId);

	List<Diary> findByIsShared(Boolean isShared);

	Optional<Diary> findByIsRepresentativeTrueAndDiaryDateAndUserId(LocalDate diaryDate, Long userId);

	List<Diary> findAllByDiaryDateAndUserId(LocalDate diaryDate, Long userId);

	List<Diary> findByUserIdAndDiaryDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}
