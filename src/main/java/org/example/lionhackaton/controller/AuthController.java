package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.oauth.AuthTokens;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.domain.oauth.JwtTokenProvider;
import org.example.lionhackaton.domain.oauth.google.GoogleLoginParams;
import org.example.lionhackaton.domain.oauth.kakao.KakaoLoginParams;
import org.example.lionhackaton.domain.oauth.naver.NaverLoginParams;
import org.example.lionhackaton.service.OAuthLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
	public ResponseEntity<?> getInfo(@AuthenticationPrincipal CustomUserDetails user) {
		User info = oAuthLoginService.getInfo(user.getId());
		return ResponseEntity.ok(info);
	}

	@PostMapping("/naver")
	public ResponseEntity<?> loginNaver(@RequestBody NaverLoginParams params) {
		return ResponseEntity.ok(oAuthLoginService.login(params));
	}

	@PostMapping("/google")
	public ResponseEntity<?> loginGoogle(@RequestBody GoogleLoginParams params) {
		return ResponseEntity.ok(oAuthLoginService.login(params));
	}
}