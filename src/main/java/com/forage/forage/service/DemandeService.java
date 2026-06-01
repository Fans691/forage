package com.forage.forage.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    private static final String STATUT_DEMANDE_ETUDE_CREE = "demande etude cree";
    private static final String STATUT_DEMANDE_ETUDE_ACCEPTE = "demande etude accepte";
    private static final String STATUT_DEMANDE_ETUDE_REFUSE = "demande etude refuse";
    private static final String STATUT_DEMANDE_FORAGE_CREE = "demande forage cree";
    private static final String STATUT_DEMANDE_FORAGE_ACCEPTE = "demande forage accepte";
    private static final String STATUT_DEMANDE_FORAGE_REFUSE = "demande forage refuse";
    private static final String STATUT_DEMANDE_TRAVAIL_CREE = "demande travail cree";
    private static final String STATUT_DEMANDE_TRAVAIL_TERMINE = "demande travail termine";

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
    public Demande updateDemande(Long id, Client client, Commune commune, LocalDateTime dateDemande, String lieu) {
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
    public Demande createDemande(Client client, Commune commune, LocalDateTime dateDemande, String lieu) {
        Demande demande = new Demande(client, commune, "", dateDemande, lieu);
        Demande savedDemande = dr.save(demande);
        enregistrerStatut(savedDemande, STATUT_DEMANDE_ETUDE_CREE);
        return savedDemande;
    }

    @Transactional
    public void marquerRefuse(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, STATUT_DEMANDE_ETUDE_REFUSE);
        }
    }

    @Transactional
    public void marquerValide(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, STATUT_DEMANDE_ETUDE_ACCEPTE);
        }
    }

	@Transactional
	public void marquerEtude(Long demandeId) {
		Demande demande = getDemandeById(demandeId);
		if (demande != null) {
			enregistrerStatut(demande, STATUT_DEMANDE_ETUDE_ACCEPTE);
		}
	}

	@Transactional
	public void marquerForage(Long demandeId) {
		Demande demande = getDemandeById(demandeId);
		if (demande != null) {
			enregistrerStatut(demande, STATUT_DEMANDE_FORAGE_CREE);
		}
	}

    @Transactional
    public void marquerForageAccepte(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, STATUT_DEMANDE_FORAGE_ACCEPTE);
        }
    }

    @Transactional
    public void marquerForageRefuse(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, STATUT_DEMANDE_FORAGE_REFUSE);
        }
    }

    @Transactional
    public void marquerTravailCree(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, STATUT_DEMANDE_TRAVAIL_CREE);
        }
    }

    @Transactional
    public void marquerTravailTermine(Long demandeId) {
        Demande demande = getDemandeById(demandeId);
        if (demande != null) {
            enregistrerStatut(demande, STATUT_DEMANDE_TRAVAIL_TERMINE);
        }
    }

    @Transactional
    public boolean changerStatutAvecDate(Long demandeId, String libelle, LocalDateTime dateStatut) {
        Demande demande = getDemandeById(demandeId);
        if (demande == null) return false;
        LocalDateTime dateFinale = dateStatut != null ? dateStatut : LocalDateTime.now();
        enregistrerStatut(demande, libelle, dateFinale);
        return true;
    }

    private void enregistrerStatut(Demande demande, String libelle) {
        enregistrerStatut(demande, libelle, LocalDateTime.now());
    }

    private void enregistrerStatut(Demande demande, String libelle, LocalDateTime dateStatut) {
        Statut statut = sr.findByLibelleIgnoreCase(libelle)
                .orElseGet(() -> sr.save(new Statut(libelle)));

        DemandeStatut demandeStatut = new DemandeStatut(statut, demande, libelle, dateStatut, 0.0);
        DemandeStatut precedent = dsr.findTopByDemandeIdAndDateLessThanEqualOrderByDateDesc(demande.getId(), dateStatut)
                .orElse(null);
        double dtMinutes = computeDtMinutes(precedent == null ? null : precedent.getDate(), dateStatut);
        demandeStatut.setDt(dtMinutes);
        dsr.save(demandeStatut);
    }

    private double computeDtMinutes(LocalDateTime previous, LocalDateTime current) {
        if (previous == null || current == null) return 0L;
        if (current.isBefore(previous)) return 0L;

        LocalTime workStart = LocalTime.of(8, 0);
        LocalTime workEnd = LocalTime.of(16, 0);
        long totalMinutes = 0L;

        LocalDate startDate = previous.toLocalDate();
        LocalDate endDate = current.toLocalDate();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                continue;
            }

            LocalDateTime dayStart = LocalDateTime.of(date, workStart);
            LocalDateTime dayEnd = LocalDateTime.of(date, workEnd);
            LocalDateTime rangeStart = previous.isAfter(dayStart) ? previous : dayStart;
            LocalDateTime rangeEnd = current.isBefore(dayEnd) ? current : dayEnd;

            if (rangeEnd.isAfter(rangeStart)) {
                totalMinutes += ChronoUnit.MINUTES.between(rangeStart, rangeEnd);
            }
        }

        return (double) totalMinutes;
    }
}
