package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CommonCalendarContentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommonCalendarContentJpaRepositoryCustom {
    public List<CommonCalendarContentResponseDto> selectCommonContentByDateTime(LocalDateTime startTargetDateTime, LocalDateTime limitTargetDateTime, Boolean isDeleted);
}
