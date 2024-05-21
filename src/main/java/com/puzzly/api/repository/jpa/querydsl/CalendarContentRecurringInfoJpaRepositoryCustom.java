package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentRecurringInfoResponseDto;

public interface CalendarContentRecurringInfoJpaRepositoryCustom {

    public CalendarContentRecurringInfoResponseDto selectCalendarContentRecurringInfo(Long contentId, Boolean isDeleted);

}
