package org.example.lionhackaton.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
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

    // Getters and Setters
    public Long getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(Long diaryId) {
        this.diaryId = diaryId;
    }

    public String getDiaryTitle() {
        return diaryTitle;
    }

    public void setDiaryTitle(String diaryTitle) {
        this.diaryTitle = diaryTitle;
    }

    public Long getSodaIndex() {
        return sodaIndex;
    }

    public void setSodaIndex(Long sodaIndex) {
        this.sodaIndex = sodaIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getGptComment() {
        return gptComment;
    }

    public void setGptComment(String gptComment) {
        this.gptComment = gptComment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsRepresentative() {
        return isRepresentative;
    }

    public void setIsRepresentative(Boolean isRepresentative) {
        this.isRepresentative = isRepresentative;
    }
}
