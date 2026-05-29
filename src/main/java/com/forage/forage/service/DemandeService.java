package com.forage.forage.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forage.forage.model.Client;
import com.forage.forage.model.Commune;
import com.forage.forage.model.Demande;
import com.forage.forage.model.DemandeStatut;
import com.forage.forage.model.Statut;
import com.forage.forage.repository.DemandeStatutRepository;
import com.forage.forage.repository.DemandeRepository;
import com.forage.forage.repository.StatutRepository;

@Service
public class DemandeService {
    protected final DemandeRepository dr;
    protected final DemandeStatutRepository dsr;
    protected final StatutRepository sr;

    public DemandeService(DemandeRepository dr, DemandeStatutRepository dsr, StatutRepository sr) {
        this.dr = dr;
        this.dsr = dsr;
        this.sr = sr;
    }

    public List<Demande> getDemande() {
        return dr.findAllWithDetails();
    }

    public Demande save(Demande d) {
        return dr.save(d);
    }

    public Demande getDemandeById(Long id) {
        return dr.findDetailedById(id).orElse(null);
    }

    public void deleteDemande(Long id) {
        dr.deleteById(id);
    }

    @Transactional
    public Demande updateDemande(Long id, Client client, Commune commune, LocalDate dateDemande, String lieu) {
        Demande demande = getDemandeById(id);

        if (demande == null) {
            return null;
        }

        demande.setClient(client);
        demande.setCommune(commune);
        demande.setDateDemande(dateDemande);
        demande.setLieu(lieu);
        return dr.save(demande);
    }

    @Transactional
    public Demande createDemande(Client client, Commune commune, LocalDate dateDemande, String lieu) {
        Demande demande = new Demande(client, commune, "", dateDemande, lieu);
        Demande savedDemande = dr.save(demande);
        enregistrerStatut(savedDemande, "envoyer");
        return savedDemande;
    }

    @Transactional
    public void marquerRefuse(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, "refuse");
        }
    }

    @Transactional
    public void marquerValide(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, "valide");
        }
    }

    private void enregistrerStatut(Demande demande, String libelle) {
        Statut statut = sr.findByLibelleIgnoreCase(libelle)
                .orElseGet(() -> sr.save(new Statut(libelle)));

        DemandeStatut demandeStatut = new DemandeStatut(statut, demande, libelle, LocalDateTime.now(), 0.0);
        dsr.save(demandeStatut);
    }
}
