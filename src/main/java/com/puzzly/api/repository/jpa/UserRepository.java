package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.User;
import com.puzzly.api.repository.jpa.querydsl.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    public User findByEmail(String email);

    public User findByUserId(Long userId);

    public User findByUserIdAndIsDeleted(Long userId, Boolean isDeleted);
}
