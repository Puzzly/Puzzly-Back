package com.puzzly.api.enums;

import lombok.Getter;
import org.apache.ibatis.type.MappedTypes;

import java.util.Arrays;

public enum AccountAuthority implements CodeEnum{
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    @Getter
    private final String stringAuthority;

    AccountAuthority(String stringAuthority){
        this.stringAuthority = stringAuthority;
    }
    public AccountAuthority getAuthorityEnum(String stringAuthority) {
        return Arrays.stream(AccountAuthority.values())
                .filter(authority -> authority.equals(stringAuthority))
                .findAny()
                .orElse(null);
    }

    @Override
    public String getText() {
        return stringAuthority;
    }

    @MappedTypes(AccountAuthority.class)
    public static class AuthTypeHandler extends EnumTypeHandler<AccountAuthority> {
        public AuthTypeHandler() {
            super(AccountAuthority.class);
        }
    }
}
