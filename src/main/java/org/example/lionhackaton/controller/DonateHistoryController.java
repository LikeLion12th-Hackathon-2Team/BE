package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.dto.request.DonateHistoryRequest;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.service.DonateHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donate-history")
@CrossOrigin("*")
public class DonateHistoryController {
	private final DonateHistoryService donateHistoryService;

	public DonateHistoryController(DonateHistoryService donateHistoryService) {
		this.donateHistoryService = donateHistoryService;
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveDonateHistory(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody DonateHistoryRequest donateHistory
	) {
		try {
			donateHistoryService.saveDonateHistory(customUserDetails, donateHistory);
			return ResponseEntity.status(HttpStatus.CREATED).body("Donation saved successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/get")
	public ResponseEntity<?> getDonateHistory(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		try {
			return ResponseEntity.ok(donateHistoryService.getDonateHistory(customUserDetails));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
