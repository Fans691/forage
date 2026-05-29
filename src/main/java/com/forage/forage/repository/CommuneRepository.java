package com.forage.forage.repository;

import com.forage.forage.model.Commune;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommuneRepository extends JpaRepository<Commune, Long> {

    @Query("select c from Commune c where c.district.id = :districtId order by c.libelle")
    List<Commune> findByDistrictId(@Param("districtId") Long districtId);
    
}
