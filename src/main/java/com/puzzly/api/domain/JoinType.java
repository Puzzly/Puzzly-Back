package com.puzzly.api.domain;

import com.puzzly.api.enums.CodeEnum;
import lombok.Getter;

import java.util.Arrays;

public enum JoinType implements CodeEnum {
    NATIVE("NATIVE"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE"),
    NAVER("NAVER");

    @Getter
    private final String joinType;

    JoinType(String joinType){
        this.joinType = joinType;
    }
    public JoinType getJoinTypeEnum(String stringJoinType) {
        return Arrays.stream(JoinType.values())
                .filter(joinType -> joinType.equals(stringJoinType))
                .findAny()
                .orElse(null);
    }


    @Override
    public String getText() {
        return joinType;
    }

}
