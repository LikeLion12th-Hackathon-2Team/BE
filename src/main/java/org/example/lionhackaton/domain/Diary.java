package org.example.lionhackaton.domain;

import java.time.LocalDateTime;

import org.example.lionhackaton.Lisenter.DiaryListener;
import org.example.lionhackaton.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(DiaryListener.class)
public class Diary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long diaryId;
	private String diaryTitle;
	private Long sodaIndex;
	private String content;
	private String purpose;

	@Column(columnDefinition = "LONGTEXT")
	private String gptComment;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean isRepresentative;
	private Boolean isFavorite=false;
	private Boolean isShared=false;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Diary(String diaryTitle, Long sodaIndex, String content, String purpose, Boolean isRepresentative, User user) {
		this.diaryTitle = diaryTitle;
		this.sodaIndex = sodaIndex;
		this.content = content;
		this.purpose = purpose;
		this.isRepresentative = isRepresentative;
		this.user = user;
	}

	public Diary() {

	}


}
