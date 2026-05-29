package com.forage.forage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.forage.forage.model.Region;
import com.forage.forage.repository.RegionRepository;

@Service
public class RegionService {
    protected final RegionRepository rr;

    public RegionService(RegionRepository rr) {
        this.rr = rr;
    }

    public List<Region> findAll() {
        return rr.findAll();
    }

}
