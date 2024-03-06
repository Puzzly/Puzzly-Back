package com.puzzly.repository;

import com.puzzly.entity.PuzzlyCal;

import java.util.List;
import java.util.Optional;

public interface CalRepository {
    PuzzlyCal save(PuzzlyCal puzzlyCal);
    Optional<PuzzlyCal> findByCalId(String calId);
    List<PuzzlyCal> findAll();
}
