package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.UserExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExRepository  extends JpaRepository<UserExtension, Long> {
}
