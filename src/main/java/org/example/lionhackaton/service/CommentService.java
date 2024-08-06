package org.example.lionhackaton.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.example.lionhackaton.domain.Comment;
import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.dto.request.CommentChooseRequest;
import org.example.lionhackaton.domain.dto.request.CommentRequest;
import org.example.lionhackaton.domain.dto.request.CommentUpdateRequest;
import org.example.lionhackaton.domain.dto.response.CommentResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.CommentRepository;
import org.example.lionhackaton.repository.DiaryRepository;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.webjars.NotFoundException;

@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final DiaryRepository diaryRepository;

	private final UserService userService;

	public CommentService(CommentRepository commentRepository, UserRepository userRepository,
		DiaryRepository diaryRepository, UserService userService) {
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.diaryRepository = diaryRepository;
		this.userService = userService;
	}

	@Transactional
	public CommentResponse saveComment(CustomUserDetails customUserDetails, CommentRequest commentRequest) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("유저를 발견하지 못했습니다."));
		Diary diary = diaryRepository.findById(commentRequest.getDiaryId())
			.orElseThrow(() -> new NotFoundException("diary를 발견하지 못했습니다."));
		System.out.println("commentRequest = " + commentRequest.getContent());
		Comment comment = new Comment();
		comment.setContent(commentRequest.getContent());
		comment.setIsChosen(false);
		comment.setUser(user);
		comment.setDiary(diary);
		comment.setNickname(user.getNickname());
		Comment save = commentRepository.save(comment);

		userService.plusCommentPoint(customUserDetails);

		return new CommentResponse(
			save.getCommentId(),
			save.getContent(),
			save.getIsChosen(),
			save.getCreatedAt(),
			save.getUpdatedAt(),
			save.getDiary().getDiaryId(),
			save.getUser().getId(),
			save.getNickname(),
			true, true, false);
	}

	public List<CommentResponse> getDiaryCommentById(Long diary_id, CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("유저를 발견하지 못했습니다."));

		return commentRepository.findByDiary_DiaryId(diary_id).stream().map(comment -> new CommentResponse(
			comment.getCommentId(),
			comment.getContent(),
			comment.getIsChosen(),
			comment.getCreatedAt(),
			comment.getUpdatedAt(),
			comment.getDiary().getDiaryId(),
			comment.getUser().getId(), comment.getNickname(),
			updateButton(user, comment.getCommentId()), deleteButton(user, diary_id, comment.getCommentId())
			, chooseButton(user, diary_id, comment.getCommentId()))).toList();
	}

	public CommentResponse getCommentById(Long id, Long comment_id, CustomUserDetails customUserDetails) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("유저를 발견하지 못했습니다."));

		Diary diary = diaryRepository.findById(id).orElseThrow(() -> new NotFoundException("diary를 찾지 못했습니다."));
		commentRepository.findByDiary_DiaryId(diary.getDiaryId()).stream().map(Comment::getCommentId)
			.filter(commentId -> commentId.equals(comment_id))
			.findFirst().orElseThrow(() -> new NotFoundException("comment를 찾지 못했습니다."));

		Comment comment = commentRepository.findById(comment_id)
			.orElseThrow(() -> new NotFoundException("comment를 찾지 못했습니다."));

		return new CommentResponse(comment.getCommentId(), comment.getContent(), comment.getIsChosen(),
			comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(), comment.getUser().getId(),
			comment.getNickname(),
			updateButton(user, comment.getCommentId()), deleteButton(user, id, comment.getCommentId())
			, chooseButton(user, id, comment.getCommentId()));
	}

	@CrossOrigin("*")
	public CommentResponse updateComment(CustomUserDetails customUserDetails,
		CommentUpdateRequest commentUpdateRequest) {
		Comment comment = commentRepository.findById(commentUpdateRequest.getCommentId())
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

		if (!diaryRepository.existsById(comment.getDiary().getDiaryId())) {
			throw new NotFoundException("해당 diary를 찾지 못했습니다");
		}

		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("user를 찾지 못했습니다."));

		comment.setContent(commentUpdateRequest.getContent());
		Comment save = commentRepository.save(comment);

		return new CommentResponse(
			save.getCommentId(),
			save.getContent(),
			save.getIsChosen(),
			save.getCreatedAt(),
			save.getUpdatedAt(),
			save.getDiary().getDiaryId(),
			save.getUser().getId(),
			save.getNickname(),
			updateButton(user, comment.getCommentId()),
			deleteButton(user, commentUpdateRequest.getDiaryId(), comment.getCommentId()),
			chooseButton(user, commentUpdateRequest.getDiaryId(), comment.getCommentId())
		);
	}

	public CommentResponse chooseComment(CustomUserDetails customUserDetails,
		CommentChooseRequest commentChooseRequest) throws AccessDeniedException {

		Diary diary = diaryRepository.findById(commentChooseRequest.getDiaryId())
			.orElseThrow(() -> new NotFoundException("diary를 찾지 못했습니다."));
		if (!diary.getIsChosen()) {
			Comment comment = commentRepository.findById(commentChooseRequest.getCommentId())
				.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

			User user = userRepository.findById(customUserDetails.getId())
				.orElseThrow(() -> new NotFoundException("user를 찾지 못했습니다."));

			if (comment.getUser().getId().equals(user.getId())) {
				throw new AccessDeniedException("자신의 댓글은 채택할 수 없습니다");
			}

			comment.setIsChosen(Boolean.TRUE);
			userService.plusChosenPoint(comment);

			diary.setIsChosen(true);
			diaryRepository.save(diary);

			Comment save = commentRepository.save(comment);

			return new CommentResponse(save.getCommentId(), save.getContent(), save.getIsChosen(), save.getCreatedAt(),
				save.getUpdatedAt(), save.getDiary().getDiaryId(), save.getUser().getId(), save.getNickname(),
				updateButton(user, comment.getCommentId()),
				deleteButton(user, commentChooseRequest.getDiaryId(), comment.getCommentId())
				, chooseButton(user, commentChooseRequest.getDiaryId(), comment.getCommentId()));
		} else {
			throw new AccessDeniedException("이미 채택된 댓글이 있습니다.");
		}
	}

	@Transactional
	public void deleteComment(CustomUserDetails customUserDetails, Long comment_id) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("user를 찾지 못했습니다."));
		Comment comment = commentRepository.findById(comment_id)
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

		userService.minusCommentPoint(customUserDetails, comment);
		commentRepository.deleteByCommentId(comment_id);
	}

	public boolean updateButton(User user, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

		return user.getId().equals(comment.getUser().getId());
	}

	public boolean deleteButton(User user, Long diaryId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

		Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new NotFoundException("diary를 찾지 못했습니다."));

		return user.getId().equals(comment.getUser().getId())
			|| user.getId().equals(diary.getUser().getId());
	}

	public boolean chooseButton(User user, Long diaryId, Long commentId) {
		Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new NotFoundException("diary를 찾지 못했습니다."));
		if(diary.getIsChosen()) {
			return false;
		}
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

		if (user.getId().equals(diary.getUser().getId())) {
			return !comment.getUser().getId().equals(user.getId());
		}
		return false;
	}

}
