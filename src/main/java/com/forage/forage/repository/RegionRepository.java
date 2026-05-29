package com.forage.forage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.forage.forage.model.Region;

public interface RegionRepository extends JpaRepository<Region,Long>{
    
}
