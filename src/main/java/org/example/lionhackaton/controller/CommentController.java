package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.Comment;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ("/api/diary/{id}/comment")
@CrossOrigin("*")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("content") String content,
            @PathVariable("id") Long diary_id
    ){
        try {
            Comment savedComment = commentService.saveComment(customUserDetails, content, diary_id);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getDiaryComment(@PathVariable("id") Long diary_id){
        try{
            List<Comment> comment = commentService.getDiaryCommentById(diary_id);
            return ResponseEntity.ok().body(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{comment_id}")
    public ResponseEntity<?> getComment(@PathVariable("comment_id") Long comment_id){
        try{
            Optional<Comment> comment = commentService.getCommentById(comment_id);
            if (comment.isPresent()) {
                return ResponseEntity.ok().body(comment);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{comment_id}/update")
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("comment_id") Long comment_id,
            @RequestParam("content") String content)
    {
        try{
            Comment updatedComment = commentService.updateComment(customUserDetails, comment_id, content);
            return ResponseEntity.status(HttpStatus.OK).body(updatedComment);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{comment_id}/choose")
    public ResponseEntity<?> chooseComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("comment_id") Long comment_id,
            @PathVariable("id") Long diary_id)
    {
        try{
            Comment chooseComment = commentService.chooseComment(customUserDetails, comment_id, diary_id);
            return ResponseEntity.status(HttpStatus.OK).body(chooseComment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{comment_id}/delete")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("comment_id") Long comment_id)
    {
        try{
            commentService.deleteComment(customUserDetails, comment_id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
