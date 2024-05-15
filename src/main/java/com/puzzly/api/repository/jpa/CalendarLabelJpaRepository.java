package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarLabel;
import com.puzzly.api.repository.jpa.querydsl.CalendarLabelJpaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarLabelJpaRepository extends JpaRepository<CalendarLabel, Long>, CalendarLabelJpaRepositoryCustom {

    CalendarLabel findByLabelName(String labelName);

}
