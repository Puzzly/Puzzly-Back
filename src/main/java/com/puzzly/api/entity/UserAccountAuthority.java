package com.puzzly.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.puzzly.api.domain.AccountAuthority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Comment("사용자 계정 권한 정보")
@Table(name="user_account_authority")
public class UserAccountAuthority {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long authorityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userId", referencedColumnName = "userId", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) private AccountAuthority accountAuthority;
}
