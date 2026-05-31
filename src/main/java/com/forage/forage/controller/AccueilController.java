package com.forage.forage.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.forage.forage.model.Client;
import com.forage.forage.model.Commune;
import com.forage.forage.model.Demande;
import com.forage.forage.model.Devis;
import com.forage.forage.service.ClientService;
import com.forage.forage.service.CommuneService;
import com.forage.forage.service.DemandeService;
import com.forage.forage.service.DistrictService;
import com.forage.forage.service.DevisService;
import com.forage.forage.service.RegionService;


@Controller
public class AccueilController {
    private final ClientService cs;
    private final DemandeService ds;
    private final CommuneService cos;
    private final RegionService rs;
    private final DistrictService dis;  
    private final DevisService dvs;

    public AccueilController(ClientService cs, DemandeService ds, CommuneService cos, RegionService rs, DistrictService dis, DevisService dvs) {
        this.cs = cs;
        this.ds = ds;
        this.cos = cos;
        this.rs = rs;
        this.dis = dis;
        this.dvs = dvs;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("clients", cs.findAll());
        model.addAttribute("regions", rs.findAll());

        return "index";
    }

    @GetMapping("/demandes")
    public String listeDemandes(Model model) {
        model.addAttribute("demandes", ds.getDemande());
        model.addAttribute("devisDemandeIds", dvs.findDemandeIds());
        return "liste";
    }

    @GetMapping("/statut")
    public String pageStatut(Model model) {
        model.addAttribute("statuts", List.of(
                "demande cree",
                "demande etude",
                "demande etude refuse",
                "demande forage"));
        return "statut";
    }

    @PostMapping("/statut")
    public String changerStatut(
            @RequestParam("ref") String ref,
            @RequestParam("statut") String statut,
            @RequestParam("date") LocalDate date,
            @RequestParam("time") String time,
            Model model) {
        model.addAttribute("statuts", List.of(
                "demande cree",
                "demande etude",
                "demande etude refuse",
                "demande forage"));

        Long demandeId = parseDemandeIdFromReference(ref);
        if (demandeId == null) {
            model.addAttribute("message", "Reference invalide.");
            return "statut";
        }

        LocalDateTime dateStatut = buildDateTime(date, time);
        if (dateStatut == null) {
            model.addAttribute("message", "Date invalide.");
            return "statut";
        }

        boolean updated = ds.changerStatutAvecDate(demandeId, statut, dateStatut);
        model.addAttribute("message", updated ? "Statut mis a jour." : "Demande introuvable.");
        return "statut";
    }

    @GetMapping("/api/demandes")
    @ResponseBody
    public List<Map<String, Object>> apiDemandes() {
        return ds.getDemande().stream()
                .map(demande -> Map.<String, Object>of(
                        "id", demande.getId(),
                        "reference", demande.getReference(),
                        "client", Map.of("nom", demande.getClient().getNom()),
                        "commune", Map.of("libelle", demande.getCommune().getLibelle()),
                "dateDemande", demande.getDateDemande() != null
                    ? demande.getDateDemande().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    : null,
                        "lieu", demande.getLieu(),
                        "statutActuelLibelle", demande.getStatutActuelLibelle()))
                .toList();
    }

    @PostMapping("/api/demandes/statut")
    @ResponseBody
    public Map<String, Object> apiMajStatutDemande(
            @RequestParam("ref") String ref,
            @RequestParam("statut") String statut,
            @RequestParam("date") String date) {
        Long demandeId = parseDemandeIdFromReference(ref);
        if (demandeId == null) {
            return Map.of("ok", false, "message", "Reference invalide.");
        }

        LocalDateTime dateStatut = parseDateTime(date);
        if (dateStatut == null) {
            return Map.of("ok", false, "message", "Date invalide (yyyy-MM-dd HH:mm).", "ref", ref);
        }

        boolean updated = ds.changerStatutAvecDate(demandeId, statut, dateStatut);
        if (!updated) {
            return Map.of("ok", false, "message", "Demande introuvable.", "ref", ref);
        }

        return Map.of("ok", true, "message", "Statut mis a jour.", "ref", ref, "statut", statut, "date", date);
    }

	@GetMapping("/devis/nouveau")
	public String nouveauDevisChoixDemande(Model model) {
		model.addAttribute("demandes", ds.getDemande());
		return "nouveau-devis";
	}

    @GetMapping("/devis/nouveau/ref")
    public String nouveauDevisParReference(@RequestParam("ref") String ref, RedirectAttributes redirectAttributes) {
        Long demandeId = parseDemandeIdFromReference(ref);
        if (demandeId == null) {
            redirectAttributes.addFlashAttribute("message", "Reference invalide.");
            return "redirect:/devis/nouveau";
        }

        Demande demande = ds.getDemandeById(demandeId);
        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/devis/nouveau";
        }

        return "redirect:/demandes/" + demandeId + "/devis/nouveau";
    }

    @GetMapping("/demandes/{id}/edit")
    public String editDemande(@PathVariable("id") Long id, Model model) {
        Demande demande = ds.getDemandeById(id);

        if (demande == null) {
            return "redirect:/demandes";
        }

        model.addAttribute("demande", demande);
        model.addAttribute("clients", cs.findAll());
        model.addAttribute("regions", rs.findAll());
        return "edit-demande";
    }

    @PostMapping("/demande")
    public String saveDemande(
            @RequestParam("clientId") Long clientId,
            @RequestParam("communeId") Long communeId,
            @RequestParam("dateDemande") LocalDate dateDemande,
            @RequestParam("timeDemande") String timeDemande,
            @RequestParam("lieu") String lieu,
            RedirectAttributes redirectAttributes) {
        Client client = cs.findById(clientId);
        Commune commune = cos.findById(communeId);

        if (client == null || commune == null) {
            redirectAttributes.addFlashAttribute("message", "Client ou commune invalide.");
            return "redirect:/";
        }

        ds.createDemande(client, commune, buildDateTime(dateDemande, timeDemande), lieu);
        redirectAttributes.addFlashAttribute("message", "Demande envoyée avec succès.");
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/{id}/update")
        public String updateDemande(
            @PathVariable("id") Long id,
            @RequestParam("clientId") Long clientId,
            @RequestParam("communeId") Long communeId,
            @RequestParam("dateDemande") LocalDate dateDemande,
            @RequestParam("timeDemande") String timeDemande,
            @RequestParam("lieu") String lieu,
            RedirectAttributes redirectAttributes) {
        Client client = cs.findById(clientId);
        Commune commune = cos.findById(communeId);

        if (client == null || commune == null) {
            redirectAttributes.addFlashAttribute("message", "Client ou commune invalide.");
            return "redirect:/demandes";
        }

        Demande demande = ds.updateDemande(id, client, commune, buildDateTime(dateDemande, timeDemande), lieu);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/demandes";
        }

        redirectAttributes.addFlashAttribute("message", "Demande modifiée avec succès.");
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/{id}/delete")
    public String deleteDemande(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        ds.deleteDemande(id);
        redirectAttributes.addFlashAttribute("message", "Demande supprimée avec succès.");
        return "redirect:/demandes";
    }

    @GetMapping("/demandes/{id}/devis/nouveau")
    public String nouveauDevis(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Demande demande = ds.getDemandeById(id);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/demandes";
        }

        java.util.List<Devis> devisList = dvs.findByDemandeId(id);
        model.addAttribute("demande", demande);
        model.addAttribute("devisList", devisList);
        return "ajout-devis";
    }

    @PostMapping("/demandes/{id}/devis")
        public String enregistrerDevis(
            @PathVariable("id") Long id,
            @RequestParam(name = "objet") List<String> objets,
            @RequestParam(name = "montant") List<Double> montants,
                @RequestParam(name = "qte", required = false) List<Double> qtes,
                @RequestParam(name = "dateDevis", required = false) LocalDate dateDevis,
                @RequestParam(name = "timeDevis", required = false) String timeDevis,
            RedirectAttributes redirectAttributes) {
        Demande demande = ds.getDemandeById(id);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/demandes";
        }

        dvs.saveForDemande(demande, objets, montants, qtes, buildDateTime(dateDevis, timeDevis));
        ds.marquerEtude(id);
        redirectAttributes.addFlashAttribute("message", "Devis enregistré avec succès.");
        return "redirect:/demandes/" + id + "/devis";
    }

    @GetMapping("/demandes/{id}/devis")
    public String voirDevis(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Demande demande = ds.getDemandeById(id);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/demandes";
        }

        java.util.List<Devis> devisList = dvs.findByDemandeId(id);
        if (devisList == null || devisList.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Aucun devis n'est encore associé à cette demande.");
            return "redirect:/demandes/" + id + "/devis/nouveau";
        }

        model.addAttribute("demande", demande);
        model.addAttribute("devisList", devisList);
        return "devis";
    }

    @PostMapping("/demandes/{id}/refuser")
    public String refuserDemande(@PathVariable("id") Long id) {
        ds.marquerRefuse(id);
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/{id}/valider")
    public String validerDemande(@PathVariable("id") Long id) {
        ds.marquerEtude(id);
        return "redirect:/demandes";
    }

    @PostMapping("/devis/{id}/accepter")
    public String accepterDevis(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Devis devis = dvs.findDetailedById(id);
        if (devis == null || devis.getDemande() == null) {
            redirectAttributes.addFlashAttribute("message", "Devis introuvable.");
            return "redirect:/demandes";
        }

        ds.marquerForage(devis.getDemande().getId());
        redirectAttributes.addFlashAttribute("message", "Demande mise en forage.");
        return "redirect:/demandes/" + devis.getDemande().getId() + "/devis";
    }

    @PostMapping("/devis/{id}/refuser")
    public String refuserDevis(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Devis devis = dvs.findDetailedById(id);
        if (devis == null || devis.getDemande() == null) {
            redirectAttributes.addFlashAttribute("message", "Devis introuvable.");
            return "redirect:/demandes";
        }

        ds.marquerRefuse(devis.getDemande().getId());
        redirectAttributes.addFlashAttribute("message", "Demande refusee.");
        return "redirect:/demandes/" + devis.getDemande().getId() + "/devis";
    }

    @PostMapping("/district")
    @ResponseBody
    public List<Map<String, Object>> getDistrictsByRegion(@RequestParam("regionId") Long regionId) {
        return dis.findByRegionId(regionId).stream()
                .map(district -> Map.<String, Object>of(
                        "id", district.getId(),
                        "libelle", district.getLibelle()))
                .toList();
    }

    @PostMapping("/commune")
    @ResponseBody
    public List<Map<String, Object>> getCommunesByDistrict(@RequestParam("districtId") Long districtId) {
        return cos.findByDistrictId(districtId).stream()
                .map(commune -> Map.<String, Object>of(
                        "id", commune.getId(),
                        "libelle", commune.getLibelle()))
                .toList();
    }

    private LocalDateTime buildDateTime(LocalDate dateDemande, String timeDemande) {
        if (dateDemande == null) return null;
        LocalTime time = LocalTime.MIDNIGHT;
        if (timeDemande != null && !timeDemande.isBlank()) {
            try {
                time = LocalTime.parse(timeDemande);
            } catch (Exception ex) {
                time = LocalTime.MIDNIGHT;
            }
        }
        return LocalDateTime.of(dateDemande, time);
    }

    private Long parseDemandeIdFromReference(String ref) {
        if (ref == null) return null;
        String digits = ref.replaceAll("\\D+", "");
        if (digits.isBlank()) return null;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDateTime.parse(date, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception ex) {
            return null;
        }
    }

    

}
