package org.example.lionhackaton.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    private String diaryTitle;
    private Long sodaIndex;
    private String content;
    private String purpose;
    private String gptComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isRepresentative;
}
