package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarResponseDto;

import java.util.List;

public interface CalendarJpaRepositoryCustom {
    public List<CalendarResponseDto> selectCalendarList(Long userId, int offset, int pageSize, boolean isDeleted);

}
