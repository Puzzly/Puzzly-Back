package com.puzzly.repository;

import com.puzzly.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<JwtToken, Long> {

    public JwtToken findByAccessTokenAndRefreshToken(String accessToken, String refreshToken);
}
