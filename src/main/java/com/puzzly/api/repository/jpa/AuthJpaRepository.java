package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthJpaRepository extends JpaRepository<JwtToken, Long> {
    public JwtToken findByAccessTokenAndRefreshToken(String accessToken, String refreshToken);
}
