package org.example.lionhackaton.service;

import java.util.List;
import java.util.Optional;

import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.dto.request.DiaryRequest;
import org.example.lionhackaton.domain.dto.response.DiaryResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.DiaryRepository;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final UserRepository userRepository;

	public DiaryService(DiaryRepository diaryRepository, UserRepository userRepository) {
		this.diaryRepository = diaryRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public DiaryResponse saveDiary(CustomUserDetails customUserDetails, DiaryRequest diaryRequest) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		Diary diary = new Diary(
			diaryRequest.getDiaryTitle(),
			diaryRequest.getSodaIndex(),
			diaryRequest.getContent(),
			diaryRequest.getPurpose(),
			diaryRequest.getIsRepresentative(),
			user);

		Diary save = diaryRepository.save(diary);

		return new DiaryResponse(save.getDiaryId(),
			save.getDiaryTitle(),
			save.getSodaIndex(),
			save.getContent(),
			save.getPurpose(),
			save.getGptComment(),
			save.getCreatedAt(),
			save.getUpdatedAt(),
			save.getIsRepresentative(),
			save.getUser().getId());
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
			diary1.getUser().getId());
	}

	public List<DiaryResponse> getAllDiaries() {
		return diaryRepository.findAll().stream().map(diary -> {
			return new DiaryResponse(
				diary.getDiaryId(),
				diary.getDiaryTitle(), diary.getSodaIndex(), diary.getContent(), diary.getPurpose(),
				diary.getGptComment(),
				diary.getCreatedAt(), diary.getUpdatedAt(), diary.getIsRepresentative(), diary.getUser().getId()
			);
		}).toList();
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
				diary.getUser().getId()))
			.toList();
	}
}
