package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.CalendarContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarContentsJpaRepository extends JpaRepository<CalendarContents, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CalendarContents SET isDeleted = true where calendar=:calendar")
    public void bulkUpdateIsDeletedCalendarContentsByCalendar(Calendar calendar);
}
