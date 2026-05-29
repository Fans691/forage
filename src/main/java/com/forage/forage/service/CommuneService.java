package com.forage.forage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.forage.forage.model.Commune;
import com.forage.forage.repository.CommuneRepository;

@Service
public class CommuneService {
    protected final CommuneRepository cr;

    public CommuneService(CommuneRepository cr) {
        this.cr = cr;
    }

    public List<Commune> findAll() {
        return cr.findAll();
    }

    public Commune findById(Long id) {
        Optional<Commune> commune = cr.findById(id);
        return commune.orElse(null);
    }

    public List<Commune> findByDistrictId(Long districtId) {
        return cr.findByDistrictId(districtId);
    }
}
