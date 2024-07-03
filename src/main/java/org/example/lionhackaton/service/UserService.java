package org.example.lionhackaton.service;

import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 사용자를 인증하는 메서드입니다.
    public boolean authenticate(String userId, String password) {
        // userId를 사용하여 데이터베이스에서 사용자 정보를 조회합니다.
        User user = userRepository.findByUserId(userId);

        // 조회한 사용자 정보가 null이 아니고, 입력된 비밀번호와 일치하는지 확인합니다.
        return user != null && user.getPassword().equals(password);
    }
}
