package com.cybergamer.tournament_service.repository;

import com.cybergamer.tournament_service.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}