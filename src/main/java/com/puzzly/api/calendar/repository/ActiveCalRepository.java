package com.puzzly.api.calendar.repository;

import com.puzzly.api.calendar.domain.PuzzlyCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveCalRepository extends JpaRepository<PuzzlyCal, Long>{
    Optional<PuzzlyCal> findByCalId(String calId);
}
