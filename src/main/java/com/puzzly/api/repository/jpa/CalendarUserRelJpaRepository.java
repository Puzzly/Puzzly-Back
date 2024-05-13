package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.CalendarUserRelation;
import com.puzzly.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarUserRelJpaRepository extends JpaRepository<CalendarUserRelation, Long> {

    public CalendarUserRelation findCalendarUserRelByUser(User user);

    public CalendarUserRelation findCalendarUserRelByUserAndCalendarAndIsDeleted(User user, Calendar calendar, boolean isDeleted);

    public List<CalendarUserRelation> findCalendarUserRelByCalendar(Calendar calendar);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CalendarUserRelation tcur set tcur.isDeleted = true where tcur.calendar =:calendar")
    public int bulkUpdateIsDeletedCalendarUserRelByCalendar(Calendar calendar);

    @Query(nativeQuery = true,
            value = "SELECT count(" +
                    "   SELECT" +
                    "       relation_id" +
                    "   FROM calendar_user_relation " +
                    "   WHERE user_id =:userId " +
                    "   AND calendar_id =:calendarId " +
                    "   AND is_deleted =:isDeleted " +
                    "   LIMIT 1" +
                    ") > 0 " +
                    "FROM DUAL"
    )
    public boolean existsCalendarUserRelation(Long userId, Long calendarId, Boolean isDeleted);
}
