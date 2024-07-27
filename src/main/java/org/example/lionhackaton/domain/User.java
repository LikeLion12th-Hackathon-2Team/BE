package org.example.lionhackaton.domain;

import java.util.Set;

import lombok.Setter;
import org.example.lionhackaton.domain.oauth.OAuthProvider;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Setter
@Entity
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String nickname;
	private Long point;
	private int dailyDiaryCount = 1;
	private int dailyCommentCount = 10;


	@Enumerated(EnumType.STRING)
	private OAuthProvider oAuthProvider;

	@OneToMany(mappedBy = "user")
	public Set<Diary> diaries;

	@OneToMany(mappedBy = "user")
	public Set<Comment> comments;

	@Builder
	public User(String email, String nickname, OAuthProvider oAuthProvider,Long point,int dailyDiaryCount,int dailyCommentCount ) {
		this.email = email;
		this.nickname = nickname;
		this.oAuthProvider = oAuthProvider;
		this.point = point;
		this.dailyDiaryCount = dailyDiaryCount;
		this.dailyCommentCount = dailyCommentCount;
	}
}
