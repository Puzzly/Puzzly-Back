package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.entity.QUserAccountAuthority;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserAccountAuthorityRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public List<AccountAuthority> selectUserAuthority(Long userId){
        QUserAccountAuthority userAccountAuthority = QUserAccountAuthority.userAccountAuthority;
        return jpaQueryFactory
                .select(userAccountAuthority.accountAuthority)
                .from(userAccountAuthority)
                .where(userAccountAuthority.user.userId.eq(userId))
                .fetch();
    }
}
