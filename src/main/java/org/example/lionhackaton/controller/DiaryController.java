package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping
    public ResponseEntity<Diary> createDiary(@RequestBody Diary diary) {
        Diary savedDiary = diaryService.saveDiary(diary);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDiary);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiary(@PathVariable Long id, @RequestBody Diary diaryDetails) {
        Optional<Diary> updatedDiary = diaryService.updateDiary(id, diaryDetails);
        return updatedDiary.map(diary -> ResponseEntity.ok().body(diary))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Diary>> getAllDiaries() {
        List<Diary> diaries = diaryService.getAllDiaries();
        return ResponseEntity.ok().body(diaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diary> getDiaryById(@PathVariable Long id) {
        Optional<Diary> diary = diaryService.getDiaryById(id);
        return diary.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id) {
        diaryService.deleteDiary(id);
        return ResponseEntity.noContent().build();
    }
}
