package org.example.lionhackaton.repository;

import org.example.lionhackaton.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
