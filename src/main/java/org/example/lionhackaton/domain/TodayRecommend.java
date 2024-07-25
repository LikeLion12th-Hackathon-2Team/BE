package org.example.lionhackaton.domain;

import java.time.LocalDateTime;

import org.example.lionhackaton.Lisenter.DiaryListener;
import org.example.lionhackaton.Lisenter.TodayRecommendListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EntityListeners(TodayRecommendListener.class)
public class TodayRecommend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(columnDefinition = "LONGTEXT")
	private String gptRecommend;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long userId;
}
