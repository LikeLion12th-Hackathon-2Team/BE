package org.example.lionhackaton.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.lionhackaton.Lisenter.CommentListener;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EntityListeners(CommentListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long commentid;

    private String content;
    private Boolean isChosen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "diary_id", referencedColumnName = "diaryid")
    private Diary diary;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
