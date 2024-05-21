package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.Calendar;
import com.puzzly.api.repository.jpa.querydsl.CalendarJpaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarJpaRepository extends JpaRepository<Calendar, Long>, CalendarJpaRepositoryCustom {
}
