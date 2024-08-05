package org.example.lionhackaton.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateRequest {
	private Long diaryId;
	private String content;
	private Long commentId;
}
