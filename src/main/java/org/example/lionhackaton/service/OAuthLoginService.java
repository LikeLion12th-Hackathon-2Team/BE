package org.example.lionhackaton.service;

import java.util.Date;
import java.util.Optional;

import org.example.lionhackaton.domain.RefreshToken;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.oauth.AuthTokens;
import org.example.lionhackaton.domain.oauth.AuthTokensGenerator;
import org.example.lionhackaton.domain.oauth.JwtTokenProvider;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthInfoResponse;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthLoginParams;
import org.example.lionhackaton.repository.RefreshTokenRepository;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
	private final UserRepository userRepository;
	private final AuthTokensGenerator authTokensGenerator;
	private final RequestOAuthInfoService requestOAuthInfoService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public AuthTokens login(OAuthLoginParams params) {
		OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
		Long userId = findOrCreateMember(oAuthInfoResponse);

		AuthTokens authTokens = authTokensGenerator.generate(userId);

		Optional<RefreshToken> byUserId = refreshTokenRepository.findByUserId(userId);
		if(byUserId.isPresent()) {
			RefreshToken refreshTokenEntity = byUserId.get();
			refreshTokenEntity.setToken(authTokens.getRefreshToken());
			refreshTokenRepository.save(refreshTokenEntity);
		} else {
			RefreshToken refreshTokenEntity = new RefreshToken();
			refreshTokenEntity.setToken(authTokens.getRefreshToken());
			refreshTokenEntity.setUserId(userId);
			refreshTokenRepository.save(refreshTokenEntity);
		}
		return authTokens;
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

	public AuthTokens refresh(String refreshToken) {
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new RuntimeException("Invalid refresh token");
		}

		Long user_id = Long.valueOf(jwtTokenProvider.extractSubject(refreshToken));

		User user = userRepository.findById(user_id)
			.orElseThrow(() -> new RuntimeException("User not found"));

		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findByToken(refreshToken);
		if (savedRefreshToken.isEmpty()) {
			throw new RuntimeException("Refresh token not found");
		}

		String newAccessToken = jwtTokenProvider.generate(
			String.valueOf(user.getId()), new Date(System.currentTimeMillis() + 3600000)); // 1시간 유효
		String newRefreshToken = jwtTokenProvider.generate(
			String.valueOf(user.getId()), new Date(System.currentTimeMillis() + 604800000)); // 7일 유효

		RefreshToken newRefreshTokenEntity = savedRefreshToken.get();
		newRefreshTokenEntity.setToken(newRefreshToken);
		refreshTokenRepository.save(newRefreshTokenEntity);

		return new AuthTokens(newAccessToken, newRefreshToken, "Bearer", 3600L);
	}

	public void logout(Long userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}
}