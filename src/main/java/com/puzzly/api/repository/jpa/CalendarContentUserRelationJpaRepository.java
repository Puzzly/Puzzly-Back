package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarContent;
import com.puzzly.api.entity.CalendarContentUserRelation;
import com.puzzly.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarContentUserRelationJpaRepository extends JpaRepository<CalendarContentUserRelation,Long> {

    @Query(nativeQuery = true,
    value = "UPDATE calendar_content_user_relation ccur "+
            "SET is_deleted =:isDeleted " +
            "WHERE ccur.user_id =:userId " +
            "AND ccur.content_id =:#{#content.contentId}")
    public int updateIsDeletedCalendarContentUserRelation(CalendarContent content, Long userId, Boolean isDeleted);

    public int deleteByCalendarContent(CalendarContent calendarContent);
}
