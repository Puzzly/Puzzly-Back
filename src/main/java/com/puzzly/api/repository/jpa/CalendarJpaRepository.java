package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarJpaRepository extends JpaRepository<Calendar, Long> {
}
