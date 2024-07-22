package org.example.lionhackaton.Lisenter;

import java.time.LocalDateTime;

import org.example.lionhackaton.domain.Diary;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class DiaryListener {

	@PrePersist
	public void prePersist(Diary diary) {
		LocalDateTime now = LocalDateTime.now();
		diary.setCreatedAt(now);
		diary.setUpdatedAt(now);
	}

	@PreUpdate
	public void preUpdate(Diary diary) {
		diary.setUpdatedAt(LocalDateTime.now());
	}
}
