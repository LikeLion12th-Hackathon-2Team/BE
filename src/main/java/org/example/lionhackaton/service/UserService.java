package src.main.java.org.example.lionhackaton.service;

import src.main.java.org.example.lionhackaton.repository.UserRepository;

import java.security.KeyStore;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean login(String Id, String Password){
        Optional<User> user = UserRepository.findByUserId(Id);
        Boolean boo = passwordEncoder.matches(password, user.get().getPassword());
        if (user.isEmpty() || !boo) {
            throw new RuntimeException("사용자를 찾지 못했습니다.");
        }
        else{
            return true;
        }
    }
}
