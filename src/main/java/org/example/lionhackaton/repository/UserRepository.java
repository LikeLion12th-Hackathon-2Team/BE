package org.example.lionhackaton.repository;

import org.example.lionhackaton.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // userId로 사용자 정보 조회
    User findByUserId(String userId);
}
