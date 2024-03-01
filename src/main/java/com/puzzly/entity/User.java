package com.puzzly.entity;

import com.puzzly.enums.Authority;
import com.puzzly.enums.JoinType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name="user_tb")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;

    private String email;
    private String password;
    private String userName;
    private LocalDate birth;
    private boolean gender;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private JoinType joinType;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    private LocalDateTime createDateTime;
    private LocalDateTime deleteDateTime;

}
