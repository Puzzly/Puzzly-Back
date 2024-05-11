package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.CalendarUserRel;
import com.puzzly.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarUserRelJpaRepository extends JpaRepository<CalendarUserRel, Long> {

    public CalendarUserRel findCalendarUserRelByUser(User user);

    public CalendarUserRel findCalendarUserRelByUserAndCalendarAndIsDeleted(User user, Calendar calendar, boolean isDeleted);

    public List<CalendarUserRel> findCalendarUserRelByCalendar(Calendar calendar);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CalendarUserRel tcur set tcur.isDeleted = true where tcur.calendar =:calendar")
    public int bulkUpdateIsDeletedCalendarUserRelByCalendar(Calendar calendar);
}
