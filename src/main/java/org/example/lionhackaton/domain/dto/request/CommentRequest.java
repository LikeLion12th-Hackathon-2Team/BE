package org.example.lionhackaton.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
	private Long diaryId;
	private String content;
}
