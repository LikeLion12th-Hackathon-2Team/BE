package org.example.lionhackaton.controller;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.lionhackaton.domain.Diary;
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
import org.webjars.NotFoundException;

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
			DiaryResponse updatedDiary = diaryService.updateDiary(customUserDetails, id, diaryDetails);
			return ResponseEntity.ok(updatedDiary);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllDiaries() {
		List<DiaryResponse> allDiaries = diaryService.getAllDiaries();
		return ResponseEntity.ok().body(allDiaries);
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

	@GetMapping("/{userId}")
	public ResponseEntity<?> getDiaryByUserId(
		@PathVariable Long user_id
	) {
		Optional<Diary> diary = diaryService.getDiaryById(user_id);
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

	@PutMapping("/favorites/{diaryId}")
	public ResponseEntity<?> toggleFavorite(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long diaryId) {
		try {
			DiaryResponse updatedDiary = diaryService.toggleFavorite(customUserDetails, diaryId);
			return ResponseEntity.ok(updatedDiary);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/shared/{diaryId}")
	public ResponseEntity<?> toggleShared(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long diaryId) {
		try {
			DiaryResponse updatedDiary = diaryService.toggleShared(customUserDetails, diaryId);
			return ResponseEntity.ok(updatedDiary);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/daily-diaries/{yearMonth}")
	public ResponseEntity<?> getDailyDiaries(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable String yearMonth) {
		try {
			YearMonth ym = YearMonth.parse(yearMonth);
			Map<Integer, Double> dailyDiaries = diaryService.getDailySodaIndexesForMonth(customUserDetails, ym);
			return ResponseEntity.ok(dailyDiaries);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/yearly-diaries/{year}")
	public ResponseEntity<?> getYearlyDiaries(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable int year) {
		try {
			Year y = Year.of(year);
			Map<Integer, Integer> diaries = diaryService.getMonthlyDiariesForYear(customUserDetails, y);
			return ResponseEntity.ok(diaries);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/favorites")
	public ResponseEntity<?> getFavoriteDiaries(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		try {
			List<DiaryResponse> favoriteDiaries = diaryService.getFavoriteDiaries(customUserDetails);
			return ResponseEntity.ok().body(favoriteDiaries);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/shared")
	public ResponseEntity<?> getSharedDiaries() {
		try {
			List<DiaryResponse> sharedDiaries = diaryService.getSharedDiaries();
			return ResponseEntity.ok().body(sharedDiaries);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}


