package com.puzzly.enums;

import lombok.Getter;

import java.util.Arrays;

public enum Authority {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    @Getter
    private final String stringAuthority;

    Authority(String stringAuthority){
        this.stringAuthority = stringAuthority;
    }
    public Authority getAuthorityEnum(String stringAuthority) {
        return Arrays.stream(Authority.values())
                .filter(authority -> authority.equals(stringAuthority))
                .findAny()
                .orElse(null);
    }
}
