package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.CalendarUserRel;
import com.puzzly.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarUserRelJpaRepository extends JpaRepository<CalendarUserRel, Long> {

    public CalendarUserRel findCalendarUserRelByUser(User user);
    public CalendarUserRel findCalendarUserRelByUserAndCalendar(User user, Calendar calendar);

    public List<CalendarUserRel> findCalendarUserRelByCalendar(Calendar calendar);

    public List<CalendarUserRel> findCalendarUserRelByCalendarAndUser(Calendar calendar, User user);
}
