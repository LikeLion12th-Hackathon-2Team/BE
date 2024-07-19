package org.example.lionhackaton.domain;

import org.example.lionhackaton.domain.oauth.OAuthProvider;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String nickname;

	@Enumerated(EnumType.STRING)
	private OAuthProvider oAuthProvider;

	@Builder
	public User(String email, String nickname, OAuthProvider oAuthProvider) {
		this.email = email;
		this.nickname = nickname;
		this.oAuthProvider = oAuthProvider;
	}
}
