package org.example.lionhackaton.service;

import org.example.lionhackaton.domain.Comment;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.CommentRepository;
import org.example.lionhackaton.repository.UserRepository;
import org.example.lionhackaton.repository.DiaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, DiaryRepository diaryRepository){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
    }

    @Transactional
    public Comment saveComment(CustomUserDetails customUserDetails, String content, Long diary_id){
        User user = userRepository.findById(customUserDetails.getId()).orElseThrow(() -> new NotFoundException("유저를 발견하지 못했습니다."));
        Diary diary = diaryRepository.findById(diary_id).orElseThrow(() -> new NotFoundException("diary를 발견하지 못했습니다."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setIsChosen(Boolean.FALSE);
        comment.setUser(user);
        comment.setDiary(diary);
        return commentRepository.save(comment);

    }

    public List<Comment> getDiaryCommentById(Long diary_id){

         return commentRepository.findByDiary_DiaryId(diary_id);
    }

    public Optional<Comment> getCommentById(Long comment_id){

        return commentRepository.findById(comment_id);
    }

    public Comment updateComment(CustomUserDetails customUserDetails, Long comment_id, String content) throws AccessDeniedException {
        Comment comment = commentRepository.findById(comment_id).orElseThrow(()->new NotFoundException("comment를 찾지못했습니다."));

        if(!diaryRepository.existsById(comment.getDiary().getDiaryId())){
            throw new NotFoundException("해당 diary를 찾지 못했습니다");
        }

        User user = userRepository.findById(customUserDetails.getId()).orElseThrow(()->new NotFoundException("user를 찾지 못했습니다."));

        if(!comment.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("수정 권한이 없습니다");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public Comment chooseComment(CustomUserDetails customUserDetails,Long comment_id,Long diary_id) throws AccessDeniedException {

        Comment comment = commentRepository.findById(comment_id).orElseThrow(()->new NotFoundException("comment를 찾지못했습니다."));
        Diary diary = diaryRepository.findById(diary_id).orElseThrow(()->new NotFoundException("diary를 찾지 못했습니다."));
        User user = userRepository.findById(customUserDetails.getId()).orElseThrow(()->new NotFoundException("user를 찾지 못했습니다."));

        if(!diary.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("채택 권한이 없습니다");
        }

        if(comment.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("수정 권한이 없습니다");
        }

        comment.setIsChosen(Boolean.TRUE);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(CustomUserDetails customUserDetails, Long comment_id) throws AccessDeniedException {
        User user = userRepository.findById(customUserDetails.getId()).orElseThrow(()->new NotFoundException("user를 찾지 못했습니다."));
        Comment comment = commentRepository.findById(comment_id).orElseThrow(()->new NotFoundException("comment를 찾지못했습니다."));

        if(!comment.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("삭제 권한이 없습니다");
        }
        commentRepository.deleteById(comment_id);
    }
}
