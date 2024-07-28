package org.example.lionhackaton.Lisenter;

import java.time.LocalDateTime;

import org.example.lionhackaton.domain.DonateHistory;

import jakarta.persistence.PrePersist;

public class DonateHistoryListener {
	@PrePersist
	public void prePersist(DonateHistory donateHistory) {
		LocalDateTime now = LocalDateTime.now();
		donateHistory.setCreatedAt(now);
	}
}
