package org.example.lionhackaton.service;

import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.repository.DiaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public Diary saveDiary(Diary diary) {
        return diaryRepository.save(diary);
    }

    public Optional<Diary> updateDiary(Long id, Diary diaryDetails) {
        return diaryRepository.findById(id).map(diary -> {
            diary.setDiaryTitle(diaryDetails.getDiaryTitle());
            diary.setSodaIndex(diaryDetails.getSodaIndex());
            diary.setContent(diaryDetails.getContent());
            diary.setPurpose(diaryDetails.getPurpose());
            diary.setGptComment(diaryDetails.getGptComment());
            diary.setCreatedAt(diaryDetails.getCreatedAt());
            diary.setUpdatedAt(diaryDetails.getUpdatedAt());
            diary.setIsRepresentative(diaryDetails.getIsRepresentative());
            return diaryRepository.save(diary);
        });
    }

    public List<Diary> getAllDiaries() {
        return diaryRepository.findAll();
    }

    public Optional<Diary> getDiaryById(Long id) {
        return diaryRepository.findById(id);
    }

    public void deleteDiary(Long id) {
        diaryRepository.deleteById(id);
    }
}
