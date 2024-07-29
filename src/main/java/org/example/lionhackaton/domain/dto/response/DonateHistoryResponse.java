package org.example.lionhackaton.domain.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DonateHistoryResponse {
	private Long donateHistoryId;
	private Long point;
	private String location;
	private Long userId;
	private LocalDateTime createdAt;
}
