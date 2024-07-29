package org.example.lionhackaton.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaryRequest {
	private String diaryTitle;
	private Long sodaIndex;
	private String content;
	private String purpose;
	private Boolean isRepresentative;
}
