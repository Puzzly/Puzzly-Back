package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentCommentResponseDto;
import com.puzzly.api.dto.response.CalendarLabelResponseDto;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface CalendarContentCommentJpaRepositoryCustom {

    public List<CalendarContentCommentResponseDto> selectCalendarContentCommentList(Long contentId, Long beforeCommentId, int pageSize);

}
