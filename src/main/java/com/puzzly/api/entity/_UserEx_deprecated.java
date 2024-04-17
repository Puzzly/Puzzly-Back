package com.puzzly.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Table(name="tb_users_ex")

public class _UserEx_deprecated {
    @Id
    private Long userExId;
    @Column
    private Long userId;
    @Column private String statusMessage;
    @Column private String profileImagePath;
    @Column private boolean termsUserInfoAgreement;
}
