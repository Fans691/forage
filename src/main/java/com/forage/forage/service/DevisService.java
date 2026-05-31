package com.forage.forage.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.forage.forage.model.Devis;
import com.forage.forage.model.Demande;
import com.forage.forage.repository.DevisRepository;

@Service
public class DevisService {
    protected final DevisRepository dr;

    public DevisService(DevisRepository dr) {
        this.dr = dr;
    }

    public Devis save(Devis devis) {
        return dr.save(devis);
    }

    public Devis findById(Long id) {
        return dr.findById(id).orElse(null);
    }

    @Transactional
    public Devis findDetailedById(Long id) {
        return dr.findDetailedById(id).orElse(null);
    }

    public List<Devis> findByDemandeId(Long demandeId) {
        return dr.findByDemandeId(demandeId);
    }

    public Set<Long> findDemandeIds() {
        List<Long> demandeIds = dr.findDemandeIds();
        return new HashSet<>(demandeIds);
    }

    @Transactional
    public List<Devis> saveForDemande(Demande demande, List<String> descriptions, List<Double> montants, LocalDate dateDevis) {
        // create and save a Devis for each provided description/montant pair
        int count = Math.min(descriptions == null ? 0 : descriptions.size(), montants == null ? 0 : montants.size());
        List<Devis> saved = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            String desc = descriptions.get(i);
            Double mont = montants.get(i);
            if (desc == null || desc.isBlank() || mont == null) continue;
            Devis devis = new Devis();
            devis.setDemande(demande);
            devis.setDescription(desc);
            devis.setMontant(mont);
            devis.setDateDevis(dateDevis);
            saved.add(dr.save(devis));
        }
        return saved;
    }

    
}
