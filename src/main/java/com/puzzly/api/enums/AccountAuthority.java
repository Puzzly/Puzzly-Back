package com.puzzly.api.enums;

import lombok.Getter;

import java.util.Arrays;

public enum AccountAuthority {
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
}
