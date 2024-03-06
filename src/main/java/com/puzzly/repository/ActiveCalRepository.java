package com.puzzly.repository;

import com.puzzly.entity.PuzzlyCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveCalRepository extends JpaRepository<PuzzlyCal, Long>, CalRepository{

}
