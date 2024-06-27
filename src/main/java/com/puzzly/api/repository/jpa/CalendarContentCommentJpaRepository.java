package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CalendarContentComment;
import com.puzzly.api.entity.CalendarLabel;
import com.puzzly.api.repository.jpa.querydsl.CalendarContentCommentJpaRepositoryCustom;
import com.puzzly.api.repository.jpa.querydsl.CalendarLabelJpaRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarContentCommentJpaRepository extends JpaRepository<CalendarContentComment, Long>,
    CalendarContentCommentJpaRepositoryCustom {

  public CalendarContentComment findTopByOrderByCommentIdDesc();

}
