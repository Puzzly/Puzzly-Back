package com.puzzly.entity;

import com.puzzly.enums.AccessType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="jwttoken_tb")
public class JwtToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    private String email;
    private String accessToken;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    public JwtToken(String email, String accessToken, String refreshToken){
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
