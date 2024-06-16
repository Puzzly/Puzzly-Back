package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CommonCalendarContent;
import com.puzzly.api.repository.jpa.querydsl.CommonCalendarContentJpaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCalendarContentJpaRepository extends JpaRepository<CommonCalendarContent, Long>, CommonCalendarContentJpaRepositoryCustom {

}
