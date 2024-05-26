package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.User;
import com.puzzly.api.repository.jpa.querydsl.UserJpaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long>, UserJpaRepositoryCustom {
    public User findByEmail(String email);

    public User findByUserId(Long userId);
}
