package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.CalendarContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarContentJpaRepository extends JpaRepository<CalendarContent, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CalendarContent SET isDeleted = true where calendar=:calendar")
    public void bulkUpdateIsDeletedCalendarContentByCalendar(Calendar calendar);
}
