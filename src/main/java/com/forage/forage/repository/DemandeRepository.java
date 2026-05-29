package com.forage.forage.repository;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.forage.forage.model.Demande;

public interface DemandeRepository extends JpaRepository<Demande,Long>{

	@EntityGraph(attributePaths = {
		"client",
		"commune",
		"commune.district",
		"commune.district.region",
		"demandeStatuts",
		"demandeStatuts.statut"
	})
	@Query("select distinct d from Demande d order by d.id desc")
	List<Demande> findAllWithDetails();

	@EntityGraph(attributePaths = {
		"client",
		"commune",
		"commune.district",
		"commune.district.region",
		"demandeStatuts",
		"demandeStatuts.statut"
	})
	Optional<Demande> findDetailedById(Long id);
    
}
