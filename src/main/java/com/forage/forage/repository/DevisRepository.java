package com.forage.forage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.forage.forage.model.Devis;

public interface DevisRepository extends JpaRepository<Devis, Long> {

	@EntityGraph(attributePaths = {
		"demande",
		"demande.client",
		"demande.commune",
		"demande.commune.district",
		"demande.commune.district.region"
	})
	List<Devis> findByDemandeId(Long demandeId);

	@Query("select d.demande.id from Devis d")
	List<Long> findDemandeIds();
    
}
