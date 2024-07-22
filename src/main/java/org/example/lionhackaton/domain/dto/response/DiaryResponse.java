package org.example.lionhackaton.domain.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiaryResponse {
	private Long diaryId;
	private String diaryTitle;
	private Long sodaIndex;
	private String content;
	private String purpose;
	private String gptComment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean isRepresentative;
	private Long userId;
}
