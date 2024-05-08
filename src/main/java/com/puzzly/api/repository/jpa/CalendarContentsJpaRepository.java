package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarContentsJpaRepository extends JpaRepository<CalendarContents, Long> {
}
