package org.example.lionhackaton.repository;

import java.util.List;

import org.example.lionhackaton.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM Comment d WHERE d.commentId = :commentId")
	void deleteByCommentId(@Param("commentId") Long commentId);
	List<Comment> findByDiary_DiaryId(Long id);
}
