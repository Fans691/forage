package com.forage.forage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.forage.forage.model.District;
import com.forage.forage.repository.DistrictRepository;

@Service
public class DistrictService {
    protected final DistrictRepository dr;

    public DistrictService(DistrictRepository dr) {
        this.dr = dr;
    }

    public List<District> findAll() {
        return dr.findAll();
    }

    public List<District> findByRegionId(Long regionId) {
        return dr.findByRegionId(regionId);
    }
}
