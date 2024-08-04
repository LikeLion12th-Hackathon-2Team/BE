package org.example.lionhackaton.controller;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.example.lionhackaton.domain.dto.request.DiaryRequest;
import org.example.lionhackaton.domain.dto.response.CommentResponse;
import org.example.lionhackaton.domain.dto.response.DiaryResponse;
import org.example.lionhackaton.domain.dto.response.MonthSodaResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
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
		@RequestBody DiaryRequest diaryDetails
	) {
		try {
			DiaryResponse updatedDiary = diaryService.updateDiary(customUserDetails, id, diaryDetails);
			return ResponseEntity.ok(updatedDiary);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllDiaries(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		List<DiaryResponse> allDiaries = diaryService.getAllDiaries(customUserDetails);
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

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDiary(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long id
	) {
		try {
			diaryService.deleteDiary(customUserDetails, id);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/favorites/{diaryId}")
	public ResponseEntity<?> toggleFavorite(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long diaryId) {
		try {
			DiaryResponse updatedDiary = diaryService.toggleFavorite(customUserDetails, diaryId);
			return ResponseEntity.ok(updatedDiary);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/shared/{diaryId}")
	public ResponseEntity<?> toggleShared(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long diaryId) {
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
			Map<Integer, MonthSodaResponse> dailyDiaries = diaryService.getDailySodaIndexesForMonth(customUserDetails,
				ym);
			if (dailyDiaries.isEmpty()) {
				return ResponseEntity.ok("null");
			}
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
			if (favoriteDiaries.isEmpty()) {
				List<DiaryResponse> list = new ArrayList<>();
				List<CommentResponse> list2 = new ArrayList<>();
				CommentResponse commentResponse = new CommentResponse(null, null, null, null, null, null, null, null,
					null, null, null);
				list2.add(commentResponse);
				DiaryResponse diaryResponse = new DiaryResponse(null, null, null, null, null, null, null, null, null,
					null, null, null, null, list2);
				list.add(diaryResponse);
				return ResponseEntity.ok(list);
			}
			return ResponseEntity.ok().body(favoriteDiaries);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/shared")
	public ResponseEntity<?> getSharedDiaries(
	) {
		try {
			List<DiaryResponse> sharedDiaries = diaryService.getSharedDiaries();
			if (sharedDiaries.isEmpty()) {
				List<DiaryResponse> list = new ArrayList<>();
				List<CommentResponse> list2 = new ArrayList<>();
				CommentResponse commentResponse = new CommentResponse(null, null, null, null, null, null, null, null,
					null, null, null);
				list2.add(commentResponse);
				DiaryResponse diaryResponse = new DiaryResponse(null, null, null, null, null, null, null, null, null,
					null, null, null, null, list2);
				list.add(diaryResponse);
				return ResponseEntity.ok(list);
			}
			return ResponseEntity.ok().body(sharedDiaries);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/daily")
	public ResponseEntity<?> getUserDailyDiaries(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestParam("date") LocalDate date
	) {
		try {
			List<DiaryResponse> dailyDiaries = diaryService.getUserDailyDiaries(customUserDetails, date);
			if (dailyDiaries.isEmpty()) {
				List<DiaryResponse> list = new ArrayList<>();
				List<CommentResponse> list2 = new ArrayList<>();
				CommentResponse commentResponse = new CommentResponse(null, null, null, null, null, null, null, null,
					null, null, null);
				list2.add(commentResponse);
				DiaryResponse diaryResponse = new DiaryResponse(null, null, null, null, null, null, null, null, null,
					null, null, null, null, list2);
				list.add(diaryResponse);
				return ResponseEntity.ok(list);
			}
			return ResponseEntity.ok().body(dailyDiaries);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}

