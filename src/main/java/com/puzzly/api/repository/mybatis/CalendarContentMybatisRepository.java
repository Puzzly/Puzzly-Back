package com.puzzly.api.repository.mybatis;

import com.puzzly.api.dto.response.CalendarContentAttachmentsResponseDto;
import com.puzzly.api.dto.response.CalendarContentRecurringInfoResponseDto;
import com.puzzly.api.dto.response.CalendarContentResponseDto;
import com.puzzly.api.dto.response.UserResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface CalendarContentMybatisRepository {

    //
    public List<CalendarContentResponseDto> selectCalendarContentByDateTimeAndCalendar(@Param("calendarId") Long calendarId, @Param("startDateTime") LocalDateTime startDateTime, @Param("limitStartDateTime") LocalDateTime limitStartDateTime, @Param("isDeleted") boolean isDeleted);
    public List<CalendarContentAttachmentsResponseDto> selectCalendarContentAttachmentsByContentId(@Param("contentId") Long contentId, @Param("isDeleted") Boolean isDeleted);

    public CalendarContentRecurringInfoResponseDto selectCalendarContentRecurringInfo(@Param("contentId") Long contentId, @Param("isDeleted") Boolean isDeleted);
    public CalendarContentResponseDto selectCalendarContentByContentId(@Param("contentId") Long contentId, @Param("isDeleted") Boolean isDeleted);
}
