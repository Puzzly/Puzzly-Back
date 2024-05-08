package com.puzzly.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.puzzly.api.domain.AccountAuthority;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
// TODO Class Builder 제거하고 AllArgsConstructor 지워야한다.
// TODO constructor builder로 가야한다.
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Table(name="tb_user_account_authorities")
public class UserAccountAuthority {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long authorityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userId", referencedColumnName = "userId", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) private AccountAuthority accountAuthority;
}
