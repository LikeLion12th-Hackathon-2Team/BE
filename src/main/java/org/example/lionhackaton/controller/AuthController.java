package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.oauth.AuthTokens;
import org.example.lionhackaton.domain.oauth.JwtTokenProvider;
import org.example.lionhackaton.domain.oauth.KakaoLoginParams;
import org.example.lionhackaton.domain.oauth.NaverLoginParams;
import org.example.lionhackaton.service.OAuthLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final OAuthLoginService oAuthLoginService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/kakao")
	public ResponseEntity<?> loginKakao(@RequestBody KakaoLoginParams params) {
		return ResponseEntity.ok(oAuthLoginService.login(params));
	}

	@GetMapping("/info")
	public ResponseEntity<?> getInfo(@RequestHeader(value = "Authorization", required = false) String token) {
		String jwtToken = token.substring(7);
		String userId = jwtTokenProvider.extractSubject(jwtToken);
		User info = oAuthLoginService.getInfo(Long.valueOf(userId));
		return ResponseEntity.ok(info);
	}

	@PostMapping("/naver")
	public ResponseEntity<AuthTokens> loginNaver(@RequestBody NaverLoginParams params) {
		return ResponseEntity.ok(oAuthLoginService.login(params));
	}
}