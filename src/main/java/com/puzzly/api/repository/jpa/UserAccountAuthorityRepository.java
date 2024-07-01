package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserAccountAuthority;
import com.puzzly.api.repository.jpa.querydsl.UserAccountAuthorityRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAccountAuthorityRepository extends JpaRepository<UserAccountAuthority, Long>, UserAccountAuthorityRepositoryCustom {

    public List<UserAccountAuthority> findByUser(User user);
    public List<UserAccountAuthority> findByAccountAuthority(String accountAuhority);
}
