package com.puzzly.api.repository.jpa.querydsl;

public interface CalendarUserRelationJpaRepositoryCustom {
    public boolean existsCalendarUserRelation(Long userId, Long calendarId, Boolean isDeleted);
}
