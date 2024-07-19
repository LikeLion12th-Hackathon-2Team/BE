package org.example.lionhackaton.service;

import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.oauth.AuthTokens;
import org.example.lionhackaton.domain.oauth.AuthTokensGenerator;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthInfoResponse;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthLoginParams;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
	private final UserRepository userRepository;
	private final AuthTokensGenerator authTokensGenerator;
	private final RequestOAuthInfoService requestOAuthInfoService;

	public AuthTokens login(OAuthLoginParams params) {
		OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
		Long userId = findOrCreateMember(oAuthInfoResponse);
		return authTokensGenerator.generate(userId);
	}

	private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
		return userRepository.findByEmail(oAuthInfoResponse.getEmail())
			.map(User::getId)
			.orElseGet(() -> newMember(oAuthInfoResponse));
	}

	private Long newMember(OAuthInfoResponse oAuthInfoResponse) {
		User member = User.builder()
			.email(oAuthInfoResponse.getEmail())
			.nickname(oAuthInfoResponse.getNickname())
			.oAuthProvider(oAuthInfoResponse.getOAuthProvider())
			.build();

		return userRepository.save(member).getId();
	}

	public User getInfo(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	}
}