package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.CalendarContents;
import com.puzzly.api.entity.CalendarContentsAttachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarContentsAttachmentsJpaRepository extends JpaRepository<CalendarContentsAttachments, Long> {
    //public CalendarContentsAttachments findByIdAndIsDeleted(Long id, Boolean isDeleted);

    //public List<CalendarContentsAttachments> findAllByCalendarContents(CalendarContents calendarContents);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
            value = "UPDATE tb_calendar_contents_attachments tcca "+
                    "SET tcca.is_deleted = true "+
                    "WHERE tcca.contents_id IN (SELECT contents_id" +
                    "                           FROM tb_calendar_contents" +
                    "                           WHERE calendar_id =:calendarId)"
            )
            // google은 update innerjoin 된다했는데..
            /*
            value = "UPDATE tb_calendar_contents_attachments tcca " +
                    "INNER JOIN tb_calendar_contents cc " +
                    "ON tcca.contents_id = cc.contents_id " +
                    "SET tcca.is_deleted = true " +
                    "WHERE cc.calendar_id IN :calendarId")
             */
    //public void bulkUpdateIsDeletedCalendarContentsAttachments(Calendar calendar);
    public void bulkUpdateIsDeletedCalendarContentsAttachments(Long calendarId);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
        value="UPDATE tb_calendar_contents_attachments tcca " +
                "SET tcca.is_deleted = true "+
                "WHERE tcca.contents_id =:contentsId"
        )
    public void bulkUpdateIsDeletedCalendarContentsAttachmentsByContentsId(Long contentsId);
}
