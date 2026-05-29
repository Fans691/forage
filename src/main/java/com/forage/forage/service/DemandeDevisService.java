package com.forage.forage.service;

import org.springframework.stereotype.Service;

import com.forage.forage.model.DemandeDevis;
import com.forage.forage.repository.DemandeDevisRepository;

import java.util.*;

@Service
public class DemandeDevisService {
    protected final DemandeDevisRepository ddr;

    public DemandeDevisService(DemandeDevisRepository ddr) {
        this.ddr = ddr;
    }

    public DemandeDevis save(DemandeDevis devis) {
        return ddr.save(devis);
    }

    public List<DemandeDevis> findAll() {
        return ddr.findAll();
    }

    public List<DemandeDevis> getByDemandeId(Long id) {
        List<DemandeDevis> ldm = findAll();
        List<DemandeDevis> result = new ArrayList<>();

        for(DemandeDevis ddvs : ldm) {
            if(ddvs.getDemande().getId() == id) {
                result.add(ddvs);
            }
        }
        
        return result;
    }
}
