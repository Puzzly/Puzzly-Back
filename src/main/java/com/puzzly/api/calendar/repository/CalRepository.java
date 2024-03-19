package com.puzzly.api.calendar.repository;

import com.puzzly.api.calendar.domain.PuzzlyCal;

import java.util.List;
import java.util.Optional;

public interface CalRepository {
    PuzzlyCal save(PuzzlyCal puzzlyCal);
    Optional<PuzzlyCal> findByCalId(String calId);
    List<PuzzlyCal> findAll();
}
