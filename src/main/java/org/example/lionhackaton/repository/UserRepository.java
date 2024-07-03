package src.main.java.org.example.lionhackaton.repository;

import jakarta.transaction.Transactional;
import org.example.lionhackaton.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUserId(String Id);
}
