package com.puzzly.api.repository.mybatis;

import com.puzzly.api.dto.response.CalendarContentsResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface CalendarContentsMybatisRepository {

    //
    public List<CalendarContentsResponseDto> selectCalendarContentsByStartDateTimeAndCalendar(@Param("calendarId") Long calendarId, @Param("startDateTime") LocalDateTime startDateTime, @Param("limitStartDateTime") LocalDateTime limitStartDateTime);
    public List<Map<String, Object>> selectCalendarContentsAttachmentsByContentsId(@Param("contentsId") Long contentsId);

    public CalendarContentsResponseDto selectCalendarContentsByContentsId(@Param("contentsId") Long contentsId);
}
