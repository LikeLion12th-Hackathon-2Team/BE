package org.example.lionhackaton.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
	private Long commentId;
	private String content;
	private Boolean isChosen;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long diaryId;
	private Long userId;

	public CommentResponse(Long commentId, String content, Boolean isChosen, LocalDateTime createdAt,
		LocalDateTime updatedAt, Long diaryId, Long userId) {
		this.commentId = commentId;
		this.content = content;
		this.isChosen = isChosen;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.diaryId = diaryId;
		this.userId = userId;
	}
}
