package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.domain.AccountAuthority;

import java.util.List;

public interface UserAccountAuthorityJpaRepositoryCustom {
    public List<AccountAuthority> selectUserAuthority(Long userId);
}
