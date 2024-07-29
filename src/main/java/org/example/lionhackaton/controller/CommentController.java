package org.example.lionhackaton.controller;

import java.util.List;

import org.example.lionhackaton.domain.dto.response.CommentResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

@RestController
@RequestMapping("/api/diary/{id}/comment")
@CrossOrigin("*")
public class CommentController {
	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	public ResponseEntity<?> createComment(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestParam("content") String content,
		@PathVariable("id") Long diary_id
	) {
		try {
			CommentResponse savedComment = commentService.saveComment(customUserDetails, content, diary_id);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getDiaryComment(
		@PathVariable("id") Long diary_id
	) {
		try {
			List<CommentResponse> comment = commentService.getDiaryCommentById(diary_id);
			return ResponseEntity.ok().body(comment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/{comment_id}")
	public ResponseEntity<?> getComment(
		@PathVariable("id") Long id,
		@PathVariable("comment_id") Long comment_id
	) {
		try {
			CommentResponse comment = commentService.getCommentById(id, comment_id);
			return ResponseEntity.ok().body(comment);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/{comment_id}/update")
	public ResponseEntity<?> updateComment(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable("comment_id") Long comment_id,
		@RequestParam("content") String content) {
		try {
			CommentResponse updatedComment = commentService.updateComment(customUserDetails, comment_id, content);
			return ResponseEntity.status(HttpStatus.OK).body(updatedComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/{comment_id}/choose")
	public ResponseEntity<?> chooseComment(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable("comment_id") Long comment_id,
		@PathVariable("id") Long diary_id) {
		try {
			CommentResponse chooseComment = commentService.chooseComment(customUserDetails, comment_id, diary_id);
			return ResponseEntity.status(HttpStatus.OK).body(chooseComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/{comment_id}/delete")
	public ResponseEntity<?> deleteComment(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable("comment_id") Long comment_id) {
		try {
			commentService.deleteComment(customUserDetails, comment_id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

}
