package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.UserEx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExJpaRepository  extends JpaRepository<UserEx, Long> {
}
