package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarContent;
import com.puzzly.api.entity.CalendarContentAttachments;
import com.puzzly.api.repository.jpa.querydsl.CalendarContentAttachmentsJpaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarContentAttachmentsJpaRepository extends JpaRepository<CalendarContentAttachments, Long>,
        CalendarContentAttachmentsJpaRepositoryCustom {
    //public CalendarContentAttachments findByIdAndIsDeleted(Long id, Boolean isDeleted);

    //public List<CalendarContentAttachments> findAllByCalendarContent(CalendarContent calendarContent);

    public List<CalendarContentAttachments> findByCalendarContentAndIsDeleted(CalendarContent calendarContent, Boolean isDeleted);
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
            value = "UPDATE calendar_content_attachments tcca "+
                    "SET tcca.is_deleted = true "+
                    "WHERE tcca.content_id IN (SELECT content_id" +
                    "                           FROM calendar_content" +
                    "                           WHERE calendar_id =:calendarId)"
            )
            // google은 update innerjoin 된다했는데..
            /*
            value = "UPDATE tb_calendar_content_attachments tcca " +
                    "INNER JOIN tb_calendar_content cc " +
                    "ON tcca.content_id = cc.content_id " +
                    "SET tcca.is_deleted = true " +
                    "WHERE cc.calendar_id IN :calendarId")
             */
    public void bulkUpdateIsDeletedCalendarContentAttachments(Long calendarId);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
        value="UPDATE calendar_content_attachments " +
                "SET is_deleted = true "+
                "WHERE content_id =:contentId"
        )
    public void bulkUpdateIsDeletedCalendarContentAttachmentsByContentId(Long contentId);
}
