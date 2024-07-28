package org.example.lionhackaton.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;
import java.time.YearMonth;

import org.example.lionhackaton.domain.ChatGPTRequest;
import org.example.lionhackaton.domain.ChatGPTResponse;
import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.dto.request.DiaryRequest;
import org.example.lionhackaton.domain.dto.response.DiaryResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
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

	@Value("${openai.model}")
	private String model;

	@Value("${openai.api.url}")
	private String apiURL;

	private final DiaryRepository diaryRepository;
	private final UserRepository userRepository;
	private final RestTemplate template;

	private final UserService userService;

	public DiaryService(RestTemplate template, DiaryRepository diaryRepository, UserRepository userRepository,UserService userService) {
		this.template = template;
		this.diaryRepository = diaryRepository;
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@Transactional
	public DiaryResponse saveDiary(CustomUserDetails customUserDetails, DiaryRequest diaryRequest) {

		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		ChatGPTRequest chatGPTRequest = getChatGPTRequest(diaryRequest);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + "sk-None-L1NGcSKoHf6WQyw1rFJoT3BlbkFJXw1grS2f76lqjp5b6ZEJ");

		HttpEntity<ChatGPTRequest> entity = new HttpEntity<>(chatGPTRequest, headers);

		ResponseEntity<ChatGPTResponse> chatGPTResponse = template.exchange(apiURL, HttpMethod.POST, entity,
			ChatGPTResponse.class);

		Diary diary = new Diary(
			diaryRequest.getDiaryTitle(),
			diaryRequest.getSodaIndex(),
			diaryRequest.getContent(),
			diaryRequest.getPurpose(),
			diaryRequest.getIsRepresentative(),
			user);

		diary.setGptComment(
			Objects.requireNonNull(chatGPTResponse.getBody()).getChoices().get(0).getMessage().getContent());

		Diary save = diaryRepository.save(diary);
		userService.plusDiaryPoint(customUserDetails);

		return new DiaryResponse(save.getDiaryId(),
			save.getDiaryTitle(),
			save.getSodaIndex(),
			save.getContent(),
			save.getPurpose(),
			save.getGptComment(),
			save.getCreatedAt(),
			save.getUpdatedAt(),
			save.getIsRepresentative(),
			save.getBookmark(),
			save.getUser().getId());
	}

	private ChatGPTRequest getChatGPTRequest(DiaryRequest diaryRequest) {
		String prompt = "[IMPORTANT] From now on, I will give all prompts in Korean. "
			+ "이제부터 너는 내가 일기를 쓰면, 그 일기를 읽고 자존감을 불어주는 역할을 하는 상담 전문가야. "
			+ "만약 내가 '오늘 시험을 못봐서 우울해'라고 적으면 너는 '그깟 시험 내가 못봐도 훨씬 잘살수있고 괜찮아!!' 이런식으로 적어주면 되는거야. "
			+ "3줄 정도로 간결하게 적어주고"
			+ "Temperature = 0.9, Top-p = 0.5, Tone = warm, Writing-style = converstaional"
			+ "이제 내가 일기의 본문을 보여줄게 \n" + diaryRequest.getContent();

		return new ChatGPTRequest(model, prompt);
	}

	@Transactional

	public DiaryResponse updateDiary(CustomUserDetails customUserDetails, Long id, Diary diaryDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		user.getDiaries().stream().map(Diary::getDiaryId).filter(diaryId -> diaryId.equals(id))
			.findFirst().orElseThrow(() -> new RuntimeException("Diary not found"));

		Diary diary1 = diaryRepository.findById(id).map(diary -> {
			diary.setDiaryTitle(diaryDetails.getDiaryTitle());
			diary.setSodaIndex(diaryDetails.getSodaIndex());
			diary.setContent(diaryDetails.getContent());
			diary.setPurpose(diaryDetails.getPurpose());
			diary.setGptComment(diaryDetails.getGptComment());
			diary.setCreatedAt(diaryDetails.getCreatedAt());
			diary.setUpdatedAt(diaryDetails.getUpdatedAt());
			diary.setIsRepresentative(diaryDetails.getIsRepresentative());
			return diaryRepository.save(diary);
		}).orElseThrow(() -> new RuntimeException("Diary not found"));

		return new DiaryResponse(
			diary1.getDiaryId(),
			diary1.getDiaryTitle(),
			diary1.getSodaIndex(),
			diary1.getContent(),
			diary1.getPurpose(),
			diary1.getGptComment(),
			diary1.getCreatedAt(),
			diary1.getUpdatedAt(),
			diary1.getIsRepresentative(),
			diary1.getBookmark(),
			diary1.getUser().getId());
	}

	public List<DiaryResponse> getAllDiaries() {
		return diaryRepository.findAll().stream().map(diary -> new DiaryResponse(
			diary.getDiaryId(),
			diary.getDiaryTitle(), diary.getSodaIndex(), diary.getContent(), diary.getPurpose(),
			diary.getGptComment(),
			diary.getCreatedAt(), diary.getUpdatedAt(), diary.getIsRepresentative(), diary.getBookmark(),
			diary.getUser().getId()
		)).toList();
	}

	public Optional<Diary> getDiaryById(Long id) {
		return diaryRepository.findById(id);
	}

	@Transactional
	public void deleteDiary(CustomUserDetails customUserDetails, Long id) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));


		user.getDiaries().stream().map(Diary::getDiaryId).filter(diaryId -> diaryId.equals(id))
			.findFirst().orElseThrow(() -> new RuntimeException("Diary not found"));

		Diary diary = diaryRepository.findById(id).orElseThrow(() -> new NotFoundException("diary not found"));

		userService.minusDiaryPoint(customUserDetails, diary);
		diaryRepository.deleteById(id);
	}

	public List<DiaryResponse> getUserAllDiaries(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		return user.getDiaries().stream()
			.map(diary -> new DiaryResponse(
				diary.getDiaryId(),
				diary.getDiaryTitle(),
				diary.getSodaIndex(),
				diary.getContent(),
				diary.getPurpose(),
				diary.getGptComment(),
				diary.getCreatedAt(),
				diary.getUpdatedAt(),
				diary.getIsRepresentative(),
				diary.getBookmark(),
				diary.getUser().getId()))
			.toList();
	}

	@Transactional
	public Diary toggleFavorite(Long userId, Long diaryId) {
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(() -> new RuntimeException("Diary not found"));

		if (!diary.getUser().getId().equals(userId)) {
			throw new RuntimeException("User not authorized to modify this diary");
		}

		diary.setIsFavorite(!diary.getIsFavorite());
		return diaryRepository.save(diary);
	}
	@Transactional
	public Diary toggleShared(Long userId, Long diaryId) {
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(() -> new RuntimeException("Diary not found"));


		if (!diary.getUser().getId().equals(userId)) {
			throw new RuntimeException("User not authorized to modify this diary");
		}

		diary.setIsShared(!diary.getIsShared());
		return diaryRepository.save(diary);
	}

	@Transactional
	public double getMonthlySodaIndex(Long userId, YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();
		List<Diary> diaries = diaryRepository.findByUserIdAndDateRange(userId, startDate, endDate);

		return diaries.stream()
				.mapToDouble(Diary::getSodaIndex)
				.average()
				.orElse(0.0);
	}
  
	public List<DiaryResponse> getBookmarkDiaries(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));

		List<DiaryResponse> diaryResponses = user.getDiaries().stream().map(diary -> {
			if (diary.getBookmark()) {
				return new DiaryResponse(
					diary.getDiaryId(),
					diary.getDiaryTitle(),
					diary.getSodaIndex(),
					diary.getContent(),
					diary.getPurpose(),
					diary.getGptComment(),
					diary.getCreatedAt(),
					diary.getUpdatedAt(),
					diary.getIsRepresentative(),
					diary.getBookmark(),
					diary.getUser().getId());
			} else {
				return null;
			}
		}).toList();

		return diaryResponses;
	}

}
