package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

@RestController
@RequestMapping("/api/point")
@CrossOrigin("*")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<?> getUserPoint(
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		try {
			Long point = userService.getPoint(customUserDetails);
			return ResponseEntity.ok(point);
		} catch (NotFoundException e) {
			return ResponseEntity.status(404).body("User not found");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/change")
	public ResponseEntity<?> updateUserPoint(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestParam("point") Long points) {
		try {
			Long point = userService.updatePoint(customUserDetails, points);
			return ResponseEntity.ok(point);
		} catch (NotFoundException e) {
			return ResponseEntity.status(404).body("User not found");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
