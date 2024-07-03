package src.main.java.org.example.lionhackaton.controller;

import src.main.java.org.example.lionhackaton.service.UserService;

import org.example.lionhackton.domain.*;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "로그인", description = "사용자의 로그인 여부를 판단");
    @GetMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam(value = "id") String userId,
            @RequestParam(value = "password") String password
    ) {
        if (userService.login(userId, password)) {
            return ResponseEntity.ok("로그인 성공");}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패.");
    }




}
