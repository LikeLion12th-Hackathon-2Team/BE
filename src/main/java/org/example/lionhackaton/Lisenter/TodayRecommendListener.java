package org.example.lionhackaton.Lisenter;

import java.time.LocalDateTime;

import org.example.lionhackaton.domain.TodayRecommend;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TodayRecommendListener {
	@PrePersist
	public void prePersist(TodayRecommend todayRecommend) {
		LocalDateTime now = LocalDateTime.now();
		todayRecommend.setCreatedAt(now);
		todayRecommend.setUpdatedAt(now);
	}

	@PreUpdate
	public void preUpdate(TodayRecommend todayRecommend) {
		todayRecommend.setUpdatedAt(LocalDateTime.now());
	}
}
