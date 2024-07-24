package org.example.lionhackaton.Lisenter;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.example.lionhackaton.domain.Comment;

import java.time.LocalDateTime;

public class CommentListener {
    @PrePersist
    public void prePersist(Comment comment) {
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(Comment comment) {
        comment.setUpdatedAt(LocalDateTime.now());
    }
}
