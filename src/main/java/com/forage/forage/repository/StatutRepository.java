package com.forage.forage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.forage.forage.model.Statut;

import java.util.Optional;

public interface StatutRepository extends JpaRepository<Statut, Long> {

	Optional<Statut> findByLibelleIgnoreCase(String libelle);
    
}
