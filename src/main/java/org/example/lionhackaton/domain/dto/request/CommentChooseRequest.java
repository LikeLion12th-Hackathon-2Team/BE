package org.example.lionhackaton.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentChooseRequest {
	private Long diaryId;
	private Long commentId;
}
