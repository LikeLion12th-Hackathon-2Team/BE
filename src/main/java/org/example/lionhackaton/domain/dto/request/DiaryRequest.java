package org.example.lionhackaton.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DiaryRequest {
	private String diaryTitle;
	private Long sodaIndex;
	private String content;
	private String purpose;
	private Boolean isRepresentative;
	private Boolean isFavorite;
	private Boolean isShared;
	private String gptComment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}