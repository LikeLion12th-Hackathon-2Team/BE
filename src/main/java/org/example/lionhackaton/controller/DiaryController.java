package org.example.lionhackaton.controller;

import java.util.List;
import java.util.Optional;

import org.example.lionhackaton.domain.diary.Diary;
import org.example.lionhackaton.domain.dto.request.DiaryRequest;
import org.example.lionhackaton.domain.dto.response.DiaryResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diary")
@CrossOrigin("*")
public class DiaryController {

	private final DiaryService diaryService;

	public DiaryController(DiaryService diaryService) {
		this.diaryService = diaryService;
	}

	@PostMapping
	public ResponseEntity<?> createDiary(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody DiaryRequest diary
	) {
		try {
			DiaryResponse savedDiary = diaryService.saveDiary(customUserDetails, diary);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedDiary);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateDiary(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long id,
		@RequestBody Diary diaryDetails
	) {
		try {
			Optional<Diary> updatedDiary = diaryService.updateDiary(customUserDetails, id, diaryDetails);
			return updatedDiary.map(diary -> ResponseEntity.ok().body(diary))
				.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllDiaries() {
		List<Diary> diaries = diaryService.getAllDiaries();
		return ResponseEntity.ok().body(diaries);
	}

	@GetMapping("/user")
	public ResponseEntity<?> getUserAllDiaries(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		try {
			List<DiaryResponse> diaries = diaryService.getUserAllDiaries(customUserDetails);
			return ResponseEntity.ok().body(diaries);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getDiaryById(
		@PathVariable Long id
	) {
		Optional<Diary> diary = diaryService.getDiaryById(id);
		return diary.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDiary(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long id
	) {
		diaryService.deleteDiary(customUserDetails, id);
		return ResponseEntity.noContent().build();
	}
}
