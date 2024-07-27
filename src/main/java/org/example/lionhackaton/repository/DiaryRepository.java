package org.example.lionhackaton.repository;

import org.example.lionhackaton.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByUserIdAndIsFavoriteTrue(Long userId);

    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId AND d.createdAt BETWEEN :startDate AND :endDate")
    List<Diary> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
}