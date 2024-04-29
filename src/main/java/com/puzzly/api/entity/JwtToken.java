package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="jwttoken_tb")
public class JwtToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    private String email;
    @Column(columnDefinition = "TEXT")
    private String accessToken;
    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    public JwtToken(String email, String accessToken, String refreshToken){
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
