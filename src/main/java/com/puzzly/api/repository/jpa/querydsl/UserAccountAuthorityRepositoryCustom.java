package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.domain.AccountAuthority;

import java.util.List;

public interface UserAccountAuthorityRepositoryCustom {
    public List<AccountAuthority> selectUserAuthority(Long userId);
}
