package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarContent;
import com.puzzly.api.entity.CalendarContentRecurringInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarContentRecurringInfoJpaRepository extends JpaRepository<CalendarContentRecurringInfo, Long> {
    public void deleteByCalendarContent(CalendarContent calendarContent);
}
