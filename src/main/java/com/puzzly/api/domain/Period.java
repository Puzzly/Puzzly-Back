package com.puzzly.api.domain;

import com.puzzly.api.enums.CodeEnum;
import lombok.Getter;

import java.util.Arrays;

public enum Period implements CodeEnum {
    DAY("DAY"),
    WEEK("WEEK"),
    MONTH("MONTH");

    @Getter
    private final String periodString;

    Period(String periodString) {
        this.periodString = periodString;
    }

    public Period getPeriodEnum(String periodString) {
        return Arrays.stream(Period.values())
                .filter(period -> period.equals(periodString))
                .findAny()
                .orElse(null);
    }

    @Override
    public String getText() {
        return periodString;
    }
}
