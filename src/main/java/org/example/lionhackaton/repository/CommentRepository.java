package org.example.lionhackaton.repository;

import org.example.lionhackaton.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDiary_DiaryId(Long id);
}
