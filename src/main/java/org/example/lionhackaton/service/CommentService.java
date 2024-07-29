package org.example.lionhackaton.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.example.lionhackaton.domain.Comment;
import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.dto.response.CommentResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.CommentRepository;
import org.example.lionhackaton.repository.DiaryRepository;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	public CommentResponse saveComment(CustomUserDetails customUserDetails, String content, Long diary_id) {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("유저를 발견하지 못했습니다."));
		Diary diary = diaryRepository.findById(diary_id).orElseThrow(() -> new NotFoundException("diary를 발견하지 못했습니다."));

		Comment comment = new Comment();
		comment.setContent(content);
		comment.setIsChosen(false);
		comment.setUser(user);
		comment.setDiary(diary);
		Comment save = commentRepository.save(comment);

		userService.plusCommentPoint(customUserDetails);

		return new CommentResponse(
			save.getCommentId(),
			save.getContent(),
			save.getIsChosen(),
			save.getCreatedAt(),
			save.getUpdatedAt(),
			save.getDiary().getDiaryId(),
			save.getUser().getId());
	}

	public List<CommentResponse> getDiaryCommentById(Long diary_id) {
		return commentRepository.findByDiary_DiaryId(diary_id).stream().map(comment -> new CommentResponse(
			comment.getCommentId(),
			comment.getContent(),
			comment.getIsChosen(),
			comment.getCreatedAt(),
			comment.getUpdatedAt(),
			comment.getDiary().getDiaryId(),
			comment.getUser().getId())).toList();
	}

	public CommentResponse getCommentById(Long id, Long comment_id) {
		Diary diary = diaryRepository.findById(id).orElseThrow(() -> new NotFoundException("diary를 찾지 못했습니다."));
		commentRepository.findByDiary_DiaryId(diary.getDiaryId()).stream().map(Comment::getCommentId)
			.filter(commentId -> commentId.equals(comment_id))
			.findFirst().orElseThrow(() -> new NotFoundException("comment를 찾지 못했습니다."));

		Comment comment = commentRepository.findById(comment_id)
			.orElseThrow(() -> new NotFoundException("comment를 찾지 못했습니다."));

		return new CommentResponse(comment.getCommentId(), comment.getContent(), comment.getIsChosen(),
			comment.getCreatedAt(), comment.getUpdatedAt(), comment.getDiary().getDiaryId(), comment.getUser().getId());
	}

	public CommentResponse updateComment(CustomUserDetails customUserDetails, Long comment_id, String content) throws
		AccessDeniedException {
		Comment comment = commentRepository.findById(comment_id)
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

		if (!diaryRepository.existsById(comment.getDiary().getDiaryId())) {
			throw new NotFoundException("해당 diary를 찾지 못했습니다");
		}

		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("user를 찾지 못했습니다."));

		if (!comment.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("수정 권한이 없습니다");
		}

		comment.setContent(content);

		Comment save = commentRepository.save(comment);
		return new CommentResponse(save.getCommentId(), save.getContent(), save.getIsChosen(), save.getCreatedAt(),
			save.getUpdatedAt(), save.getDiary().getDiaryId(), save.getUser().getId());
	}

	public CommentResponse chooseComment(CustomUserDetails customUserDetails, Long comment_id, Long diary_id) throws
		AccessDeniedException {

		Comment comment = commentRepository.findById(comment_id)
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));
		Diary diary = diaryRepository.findById(diary_id).orElseThrow(() -> new NotFoundException("diary를 찾지 못했습니다."));
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("user를 찾지 못했습니다."));

		if (!diary.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("채택 권한이 없습니다");
		}

		if (comment.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("자신의 댓글은 채택할 수 없습니다");
		}

		comment.setIsChosen(Boolean.TRUE);
		userService.plusChosenPoint(comment);

		Comment save = commentRepository.save(comment);
		return new CommentResponse(save.getCommentId(), save.getContent(), save.getIsChosen(), save.getCreatedAt(),
			save.getUpdatedAt(), save.getDiary().getDiaryId(), save.getUser().getId());
	}

	@Transactional
	public void deleteComment(CustomUserDetails customUserDetails, Long comment_id) throws AccessDeniedException {
		User user = userRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new NotFoundException("user를 찾지 못했습니다."));
		Comment comment = commentRepository.findById(comment_id)
			.orElseThrow(() -> new NotFoundException("comment를 찾지못했습니다."));

		if (!comment.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("삭제 권한이 없습니다");
		}

		userService.minusCommentPoint(customUserDetails, comment);
		commentRepository.deleteById(comment_id);
	}
}
