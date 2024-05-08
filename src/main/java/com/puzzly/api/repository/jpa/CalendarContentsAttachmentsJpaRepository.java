package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarContents;
import com.puzzly.api.entity.CalendarContentsAttachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarContentsAttachmentsJpaRepository extends JpaRepository<CalendarContentsAttachments, Long> {
    //public CalendarContentsAttachments findByIdAndIsDeleted(Long id, Boolean isDeleted);

    //public List<CalendarContentsAttachments> findAllByCalendarContents(CalendarContents calendarContents);
}
