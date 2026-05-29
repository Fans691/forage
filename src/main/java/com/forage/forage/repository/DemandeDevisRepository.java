package com.forage.forage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.forage.forage.model.DemandeDevis;

public interface DemandeDevisRepository extends JpaRepository<DemandeDevis, Long> {
    
}
