package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarInviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarInviteCodeJpaRepository extends JpaRepository<CalendarInviteCode, Long> {

    public CalendarInviteCode findCalendarInviteCodeByInviteCode(String inviteCode);
}
