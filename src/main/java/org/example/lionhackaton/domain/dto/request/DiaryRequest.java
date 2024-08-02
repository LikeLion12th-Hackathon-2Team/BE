package org.example.lionhackaton.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DiaryRequest {
	private String diaryTitle;
	private Long sodaIndex;
	private String content;
	private String purpose;
	private Boolean isRepresentative;
	private Boolean isFavorite;
	private Boolean isShared;
	private LocalDate diaryDate;
}
