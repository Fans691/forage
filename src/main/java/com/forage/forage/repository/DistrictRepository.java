package com.forage.forage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.forage.forage.model.District;

public interface DistrictRepository extends JpaRepository<District, Long>{

    @Query("select d from District d where d.region.id = :regionId order by d.libelle")
    List<District> findByRegionId(@Param("regionId") Long regionId);
    
}
