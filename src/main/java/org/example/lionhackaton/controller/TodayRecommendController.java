package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.service.TodayRecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/today_recommend")
@CrossOrigin("*")
public class TodayRecommendController {
	private final TodayRecommendService todayRecommendService;

	public TodayRecommendController(TodayRecommendService todayRecommendService) {
		this.todayRecommendService = todayRecommendService;
	}

	@GetMapping()
	public ResponseEntity<?> getTodayRecommend(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		try {
			String todayRecommend = todayRecommendService.getTodayRecommend(customUserDetails);
			return ResponseEntity.ok().body(todayRecommend);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
}
