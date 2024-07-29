package org.example.lionhackaton.service;

import java.time.LocalDate;
import java.util.List;

import org.example.lionhackaton.domain.Comment;
import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void resetUserColumn() {
		List<User> users = userRepository.findAll();
		userRepository.saveAll(users.stream().map(this::resetUserCount).toList());
	}

	public Long getPoint(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		return user.getPoint();
	}

	public Long updatePoint(CustomUserDetails customUserDetails, Long points) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		user.setPoint(points);
		userRepository.save(user);

		return user.getPoint();
	}

	public void plusDiaryPoint(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		if (user.getDailyDiaryCount() == 1) {
			user.setPoint(user.getPoint() + 100);
			user.setDailyDiaryCount(0);
		}
		userRepository.save(user);
	}

	public void minusDiaryPoint(CustomUserDetails customUserDetails, Diary diary) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		LocalDate today = LocalDate.now();
		List<Diary> todayDiaries = user.getDiaries()
			.stream()
			.filter(diary1 -> diary1.getCreatedAt().toLocalDate().equals(today))
			.toList();

		if (todayDiaries.size() == 1 && todayDiaries.contains(diary)) {
			user.setPoint(user.getPoint() - 100);
			user.setDailyDiaryCount(1);
		}

		userRepository.save(user);
	}

	public void plusCommentPoint(CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		if (user.getDailyCommentCount() >= 1) {
			user.setPoint(user.getPoint() + 10);
			user.setDailyCommentCount(user.getDailyCommentCount() - 1);
		}
		userRepository.save(user);
	}

	public void minusCommentPoint(CustomUserDetails customUserDetails, Comment comment) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		LocalDate today = LocalDate.now();
		List<Comment> todayComments = user.getComments()
			.stream()
			.filter(comments -> comments.getCreatedAt().toLocalDate().equals(today))
			.toList();

		if (todayComments.size() <= 10 && todayComments.contains(comment)) {
			user.setPoint(user.getPoint() - 10);
			user.setDailyDiaryCount(user.getDailyCommentCount() + 1);
		}
		if (comment.getIsChosen()) {
			user.setPoint(user.getPoint() - 30);
		}

		userRepository.save(user);
	}

	public void plusChosenPoint(Comment comment) {
		User user = userRepository.findById(comment.getUser().getId())
			.orElseThrow(() -> new NotFoundException("user를 찾지 못했습니다."));

		user.setPoint(user.getPoint() + 30);
		userRepository.save(user);
	}

	private User resetUserCount(User user) {
		user.setDailyDiaryCount(1);
		user.setDailyCommentCount(10);
		return user;
	}

}
