package org.example.lionhackaton.domain;

import java.time.LocalDateTime;

import org.example.lionhackaton.Lisenter.DiaryListener;
import org.example.lionhackaton.Lisenter.DonateHistoryListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(DonateHistoryListener.class)
public class DonateHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long donateHistoryId;
	private Long point;
	private String location;
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
