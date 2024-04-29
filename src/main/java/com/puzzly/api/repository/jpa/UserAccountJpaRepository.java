package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserAccountAuthority;
import com.puzzly.api.entity.UserEx;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountAuthority, Long> {

    public List<UserAccountAuthority> findByUser(User user);
    public List<UserAccountAuthority> findByAccountAuthority(String accountAuhority);
}
