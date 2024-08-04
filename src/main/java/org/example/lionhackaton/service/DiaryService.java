package org.example.lionhackaton.service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.example.lionhackaton.domain.ChatGPTRequest;
import org.example.lionhackaton.domain.ChatGPTResponse;
import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.dto.request.DiaryRequest;
import org.example.lionhackaton.domain.dto.response.CommentResponse;
import org.example.lionhackaton.domain.dto.response.DiaryResponse;
import org.example.lionhackaton.domain.dto.response.MonthSodaResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.CommentRepository;
import org.example.lionhackaton.repository.DiaryRepository;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

@Service
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final UserRepository userRepository;
	private final RestTemplate template;

	private final CommentService commentService;
	private final UserService userService;
	@Value("${openai.model}")
	private String model;
	@Value("${openai.api.url}")
	private String apiURL;

	private final CommentRepository commentRepository;

	public DiaryService(RestTemplate template, DiaryRepository diaryRepository, UserRepository userRepository,
		UserService userService, CommentRepository commentRepository, CommentService commentService) {
		this.template = template;
		this.diaryRepository = diaryRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.commentRepository = commentRepository;
		this.commentService = commentService;
	}

	@Transactional
	public DiaryResponse saveDiary(CustomUserDetails customUserDetails, DiaryRequest diaryRequest) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		getGptComment(diaryRequest.getContent());
		if (diaryRequest.getDiaryDate() != null) {
			diaryRequest.setDiaryDate(LocalDate.now());
		}
		LocalDate startOfDay = diaryRequest.getDiaryDate();

		if (diaryRequest.getIsRepresentative()) {
			diaryRepository.findByIsRepresentativeTrueAndDiaryDateAndUserId(startOfDay, customUserDetails.getId())
				.ifPresent(
					diary -> {
						diary.setIsRepresentative(false);
						diaryRepository.save(diary);
					}
				);
		} else {
			if (diaryRepository.findAllByDiaryDateAndUserId(startOfDay, customUserDetails.getId()).isEmpty()) {
				diaryRequest.setIsRepresentative(true);
			}
		}

		if (!diaryRequest.getPurpose().equals("red") && !diaryRequest.getPurpose().equals("yellow")
			&& !diaryRequest.getPurpose().equals("blue") && !diaryRequest.getPurpose().equals("green")) {
			throw new RuntimeException("Purpose must be one of 'red', 'yellow', 'blue', 'green'");
		}

		Diary diary = new Diary(
			diaryRequest.getDiaryTitle(),
			diaryRequest.getSodaIndex(),
			diaryRequest.getContent(),
			diaryRequest.getPurpose(),
			diaryRequest.getIsRepresentative(),
			diaryRequest.getIsFavorite(),
			diaryRequest.getIsShared(),
			diaryRequest.getDiaryDate(),
			user);

		diary.setGptComment(getGptComment(diaryRequest.getContent()));

		Diary save = diaryRepository.save(diary);
		userService.plusDiaryPoint(customUserDetails);

		return getDiaryResponse(user, save);
	}

	private String getGptComment(String content) {
		ChatGPTRequest chatGPTRequest = getChatGPTRequest(content);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + "sk-None-L1NGcSKoHf6WQyw1rFJoT3BlbkFJXw1grS2f76lqjp5b6ZEJ");

		HttpEntity<ChatGPTRequest> entity = new HttpEntity<>(chatGPTRequest, headers);

		ResponseEntity<ChatGPTResponse> chatGPTResponse = template.exchange(apiURL, HttpMethod.POST, entity,
			ChatGPTResponse.class);

		return Objects.requireNonNull(chatGPTResponse.getBody()).getChoices().get(0).getMessage().getContent();
	}

	private ChatGPTRequest getChatGPTRequest(String content) {
		String prompt = "[IMPORTANT] From now on, I will give all prompts in Korean. "
			+ "이제부터 너는 내가 일기를 쓰면, 그 일기를 읽고 자존감을 불어주는 역할을 하는 상담 전문가야. "
			+ "만약 내가 '오늘 시험을 못봐서 우울해'라고 적으면 너는 '그깟 시험 내가 못봐도 훨씬 잘살수있고 괜찮아!!' 이런식으로 적어주면 되는거야. "
			+ "3줄 정도로 간결하게 적어주고"
			+ "Temperature = 0.9, Top-p = 0.5, Tone = warm, Writing-style = converstaional"
			+ "이제 내가 일기의 본문을 보여줄게 \n" + content;

		return new ChatGPTRequest(model, prompt);
	}

	@Transactional
	public DiaryResponse updateDiary(CustomUserDetails customUserDetails, Long id, DiaryRequest diaryDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		user.getDiaries().stream().map(Diary::getDiaryId).filter(diaryId -> diaryId.equals(id))
			.findFirst().orElseThrow(() -> new RuntimeException("Diary not found"));

		Diary diary1 = diaryRepository.findById(id).map(diary -> {
			diary.setDiaryTitle(
				(diaryDetails.getDiaryTitle() != null) ? diaryDetails.getDiaryTitle() : diary.getDiaryTitle());
			diary.setSodaIndex(
				(diaryDetails.getSodaIndex() != null) ? diaryDetails.getSodaIndex() : diary.getSodaIndex());
			if (diaryDetails.getContent() != null) {
				diary.setGptComment(getGptComment(diaryDetails.getContent()));
			}
			diary.setContent((diaryDetails.getContent() != null) ? diaryDetails.getContent() : diary.getContent());
			diary.setPurpose((diaryDetails.getPurpose() != null) ? diaryDetails.getPurpose() : diary.getPurpose());
			diary.setDiaryDate(
				(diaryDetails.getDiaryDate() != null) ? diaryDetails.getDiaryDate() : diary.getDiaryDate());
			if (diaryDetails.getIsRepresentative() != null && !diaryDetails.getIsRepresentative()) {
				diaryRepository.findAllByDiaryDateAndUserId(diary.getDiaryDate(), user.getId()).stream()
					.filter(d -> !d.getIsRepresentative())
					.findFirst()
					.ifPresent(diary2 -> {
						diary2.setIsRepresentative(true);
						diaryRepository.save(diary2);
					});
			}
			diary.setIsRepresentative(
				(diaryDetails.getIsRepresentative() != null) ? diaryDetails.getIsRepresentative() :
					diary.getIsRepresentative());
			return diaryRepository.save(diary);
		}).orElseThrow(() -> new RuntimeException("Diary not found"));

		return getDiaryResponse(user, diary1);
	}

	public List<DiaryResponse> getAllDiaries(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		return diaryRepository.findAll().stream()
			.map(diary -> getDiaryResponse(user, diary))
			.toList();
	}

	private DiaryResponse getDiaryResponse(User user, Diary diary) {
		List<CommentResponse> list = new ArrayList<>(commentRepository.findByDiary_DiaryId(diary.getDiaryId())
			.stream()
			.map(comment -> new CommentResponse(comment.getCommentId(), comment.getContent(), comment.getIsChosen(),
				comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(),
				comment.getUser().getId(), comment.getNickname(),
				commentService.updateButton(user, comment.getCommentId()),
				commentService.deleteButton(user, diary.getDiaryId(), comment.getCommentId()),
				commentService.chooseButton(user, diary.getDiaryId())))
			.toList());

		if (list.isEmpty()) {
			list.add(new CommentResponse(null, null, null, null, null, null, null, null, null, null, null));
		}

		return new DiaryResponse(
			diary.getDiaryId(),
			diary.getDiaryTitle(), diary.getSodaIndex(), diary.getContent(), diary.getPurpose(),
			diary.getGptComment(), diary.getDiaryDate(),
			diary.getCreatedAt(), diary.getUpdatedAt(), diary.getIsRepresentative(), diary.getIsShared(),
			diary.getIsFavorite(),
			diary.getUser().getId(),
			list
		);
	}

	@Transactional
	public void deleteDiary(CustomUserDetails customUserDetails, Long id) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		user.getDiaries().stream().map(Diary::getDiaryId).filter(diaryId -> diaryId.equals(id))
			.findFirst().orElseThrow(() -> new RuntimeException("Diary not found"));

		Diary diary = diaryRepository.findById(id).orElseThrow(() -> new NotFoundException("diary not found"));

		userService.minusDiaryPoint(customUserDetails, diary);
		diaryRepository.deleteByDiaryId(id);
	}

	public List<DiaryResponse> getUserAllDiaries(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		return user.getDiaries().stream()
			.map(diary -> {
					List<CommentResponse> list = new ArrayList<>(
						commentRepository.findByDiary_DiaryId(diary.getDiaryId())
							.stream()
							.map(comment -> new CommentResponse(comment.getCommentId(), comment.getContent(),
								comment.getIsChosen(),
								comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(),
								comment.getUser().getId(), comment.getNickname()
								, commentService.updateButton(user, comment.getCommentId()),
								commentService.deleteButton(user, diary.getDiaryId(), comment.getCommentId()),
								commentService.chooseButton(user, diary.getDiaryId())))
							.toList());

					if (list.isEmpty()) {
						list.add(new CommentResponse(null, null, null, null, null, null, null, null, null, null, null));
					}

					return new DiaryResponse(
						diary.getDiaryId(),
						diary.getDiaryTitle(),
						diary.getSodaIndex(),
						diary.getContent(),
						diary.getPurpose(),
						diary.getGptComment(),
						diary.getDiaryDate(),
						diary.getCreatedAt(),
						diary.getUpdatedAt(),
						diary.getIsRepresentative(),
						diary.getIsShared(),
						diary.getIsFavorite(),
						diary.getUser().getId(),
						list);
				}
			)

			.toList();
	}

	@Transactional
	public DiaryResponse toggleFavorite(CustomUserDetails customUserDetails, Long diaryId) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new RuntimeException("Diary not found"));

		if (!diary.getUser().getId().equals(customUserDetails.getId())) {
			throw new RuntimeException("User not authorized to modify this diary");
		}

		diary.setIsFavorite(!diary.getIsFavorite());

		Diary save = diaryRepository.save(diary);

		List<CommentResponse> list = new ArrayList<>(commentRepository.findByDiary_DiaryId(save.getDiaryId())
			.stream()
			.map(comment -> new CommentResponse(comment.getCommentId(), comment.getContent(), comment.getIsChosen(),
				comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(),
				comment.getUser().getId(), comment.getNickname(),
				commentService.updateButton(user, comment.getCommentId()),
				commentService.deleteButton(user, diary.getDiaryId(), comment.getCommentId()),
				commentService.chooseButton(user, diary.getDiaryId())))
			.toList());

		return getDiaryResponse(save, list);
	}

	@Transactional
	public DiaryResponse toggleShared(CustomUserDetails customUserDetails, Long diaryId) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new RuntimeException("Diary not found"));

		if (!diary.getUser().getId().equals(customUserDetails.getId())) {
			throw new RuntimeException("User not authorized to modify this diary");
		}

		diary.setIsShared(!diary.getIsShared());
		Diary save = diaryRepository.save(diary);

		List<CommentResponse> list = commentRepository.findByDiary_DiaryId(save.getDiaryId())
			.stream()
			.map(comment -> new CommentResponse(comment.getCommentId(), comment.getContent(), comment.getIsChosen(),
				comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(),
				comment.getUser().getId(), comment.getNickname(),
				commentService.updateButton(user, comment.getCommentId()),
				commentService.deleteButton(user, diary.getDiaryId(), comment.getCommentId()),
				commentService.chooseButton(user, diary.getDiaryId())))
			.toList();

		return getDiaryResponse(save, list);
	}

	private DiaryResponse getDiaryResponse(Diary save, List<CommentResponse> list) {
		if (list.isEmpty()) {
			list.add(new CommentResponse(null, null, null, null, null, null, null, null, null, null, null));
		}

		return new DiaryResponse(save.getDiaryId(),
			save.getDiaryTitle(),
			save.getSodaIndex(),
			save.getContent(),
			save.getPurpose(),
			save.getGptComment(),
			save.getDiaryDate(),
			save.getCreatedAt(),
			save.getUpdatedAt(),
			save.getIsRepresentative(),
			save.getIsShared(),
			save.getIsFavorite(),
			save.getUser().getId(), list);
	}

	@Transactional
	public Map<Integer, MonthSodaResponse> getDailySodaIndexesForMonth(CustomUserDetails customUserDetails,
		YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();

		List<Diary> diaries = diaryRepository.findByUserIdAndDiaryDateBetween(customUserDetails.getId(), startDate,
				endDate)
			.stream()
			.filter(Diary::getIsRepresentative)
			.toList();

		Map<Integer, MonthSodaResponse> sodaIndexes = new HashMap<>();

		for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
			sodaIndexes.put(day, new MonthSodaResponse("null", null));
		}

		diaries.forEach(diary -> {
			int dayOfMonth = diary.getDiaryDate().getDayOfMonth();
			sodaIndexes.put(dayOfMonth, new MonthSodaResponse(
				diary.getPurpose(), diary.getSodaIndex()));
		});

		return sodaIndexes;
	}

	@Transactional
	public Map<Integer, Integer> getMonthlyDiariesForYear(CustomUserDetails customUserDetails, Year year) {
		Map<Integer, Integer> yearlyDiaries = new HashMap<>();

		for (int month = 1; month <= 12; month++) {
			YearMonth yearMonth = YearMonth.of(year.getValue(), month);
			LocalDate startDate = yearMonth.atDay(1);
			LocalDate endDate = yearMonth.atEndOfMonth();
			int monthlyDiaries = diaryRepository.findByUserIdAndDiaryDateBetween(
				customUserDetails.getId(), startDate, endDate).size();
			yearlyDiaries.put(month, monthlyDiaries);
		}

		return yearlyDiaries;
	}

	public List<DiaryResponse> getFavoriteDiaries(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));

		return user.getDiaries().stream()
			.filter(Diary::getIsFavorite)
			.map(diary -> {
				List<CommentResponse> list = new ArrayList<>(commentRepository.findByDiary_DiaryId(diary.getDiaryId())
					.stream()
					.map(comment -> new CommentResponse(comment.getCommentId(), comment.getContent(),
						comment.getIsChosen(),
						comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(),
						comment.getUser().getId(), comment.getNickname(),
						commentService.updateButton(user, comment.getCommentId()),
						commentService.deleteButton(user, diary.getDiaryId(), comment.getCommentId()),
						commentService.chooseButton(user, diary.getDiaryId())))
					.toList());

				if (list.isEmpty()) {
					list.add(new CommentResponse(null, null, null, null, null, null, null, null, null, null, null));
				}

				return new DiaryResponse(
					diary.getDiaryId(),
					diary.getDiaryTitle(),
					diary.getSodaIndex(),
					diary.getContent(),
					diary.getPurpose(),
					diary.getGptComment(),
					diary.getDiaryDate(),
					diary.getCreatedAt(),
					diary.getUpdatedAt(),
					diary.getIsRepresentative(),
					diary.getIsFavorite(),
					diary.getIsShared(),
					diary.getUser().getId(),
					list);
			})
			.toList();
	}

	public List<DiaryResponse> getSharedDiaries() {
		List<Diary> sharedDiaries = diaryRepository.findByIsShared(true);
		List<DiaryResponse> sharedDiariesResponse = new ArrayList<>();
		for (Diary diary : sharedDiaries) {
			List<CommentResponse> list = new ArrayList<>(
				diary.getComments().stream().map(comment -> new CommentResponse(
					comment.getCommentId(),
					comment.getContent(),
					comment.getIsChosen(),
					comment.getCreatedAt(),
					comment.getUpdatedAt(),
					comment.getDiary().getDiaryId(),
					comment.getUser().getId(),
					comment.getNickname(),
					commentService.updateButton(diary.getUser(), comment.getCommentId()),
					commentService.deleteButton(diary.getUser(), diary.getDiaryId(), comment.getCommentId()),
					commentService.chooseButton(diary.getUser(), diary.getDiaryId())
				)).toList());

			if (list.isEmpty()) {
				list.add(new CommentResponse(null, null, null, null, null, null, null, null, null, null, null));
			}

			sharedDiariesResponse.add(new DiaryResponse(
				diary.getDiaryId(),
				diary.getDiaryTitle(),
				diary.getSodaIndex(),
				diary.getContent(),
				diary.getPurpose(),
				diary.getGptComment(),
				diary.getDiaryDate(),
				diary.getCreatedAt(),
				diary.getUpdatedAt(),
				diary.getIsRepresentative(),
				diary.getIsFavorite(),
				diary.getIsShared(),
				diary.getUser().getId(),
				list
			));
		}

		return sharedDiariesResponse;
	}

	public List<DiaryResponse> getUserDailyDiaries(CustomUserDetails customUserDetails, LocalDate date) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));

		return user.getDiaries().stream()
			.filter(diary -> diary.getDiaryDate().equals(date))
			.map(diary -> {
				List<CommentResponse> list = new ArrayList<>(commentRepository.findByDiary_DiaryId(diary.getDiaryId())
					.stream()
					.map(comment -> new CommentResponse(comment.getCommentId(), comment.getContent(),
						comment.getIsChosen(),
						comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(),
						comment.getUser().getId(), comment.getNickname(),
						commentService.updateButton(user, comment.getCommentId()),
						commentService.deleteButton(user, diary.getDiaryId(), comment.getCommentId()),
						commentService.chooseButton(user, diary.getDiaryId())))
					.toList());

				if (list.isEmpty()) {
					list.add(new CommentResponse(null, null, null, null, null, null, null, null, null, null, null));
				}

				return new DiaryResponse(
					diary.getDiaryId(),
					diary.getDiaryTitle(),
					diary.getSodaIndex(),
					diary.getContent(),
					diary.getPurpose(),
					diary.getGptComment(),
					diary.getDiaryDate(),
					diary.getCreatedAt(),
					diary.getUpdatedAt(),
					diary.getIsRepresentative(),
					diary.getIsFavorite(),
					diary.getIsShared(),
					diary.getUser().getId(),
					list);
			})
			.toList();
	}
}
