package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentAttachmentsResponseDto;

import java.util.List;

public interface CalendarContentAttachmentsJpaRepositoryCustom {
    public List<CalendarContentAttachmentsResponseDto> selectCalendarContentAttachmentsByContentId(Long contentId, Boolean isDeleted);
}
