package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.CommonCalendarSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCalendarSyncJpaRepository extends JpaRepository<CommonCalendarSync, Long>{

    public CommonCalendarSync findBySyncYearAndSyncMonth(int year, int month);
}
