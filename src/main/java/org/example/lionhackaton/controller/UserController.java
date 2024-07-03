package org.example.lionhackaton.controller;

import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        // UserService를 사용자 인증
        boolean isAuthenticated = userService.authenticate(user.getUserId(), user.getPassword());

        // 인증 결과에 따라 응답받기
        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful"); // 인증 성공 시 200 OK 응답을 반환합니다.
        } else {
            return ResponseEntity.status(401).body("Invalid credentials"); // 인증 실패 시 401 Unauthorized 응답을 반환합니다.
        }
    }
}
