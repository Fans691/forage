package com.forage.forage.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.forage.forage.model.DemandeStatut;

public interface DemandeStatutRepository extends JpaRepository<DemandeStatut, Long>{
	Optional<DemandeStatut> findTopByDemandeIdAndDateLessThanEqualOrderByDateDesc(Long demandeId, LocalDateTime date);
}
