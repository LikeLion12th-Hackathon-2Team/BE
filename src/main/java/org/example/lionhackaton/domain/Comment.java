package org.example.lionhackaton.domain;

import java.time.LocalDateTime;

import org.example.lionhackaton.Lisenter.CommentListener;

import io.swagger.v3.oas.annotations.media.Schema;
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
@EntityListeners(CommentListener.class)
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	private String content;
	private Boolean isChosen;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "diary_id", referencedColumnName = "diaryId")
	private Diary diary;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

}
