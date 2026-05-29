package com.forage.forage.controller;

import java.time.LocalDate;
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

    @GetMapping("/demandes/{id}/edit")
    public String editDemande(@PathVariable Long id, Model model) {
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
            @RequestParam("lieu") String lieu,
            RedirectAttributes redirectAttributes) {
        Client client = cs.findById(clientId);
        Commune commune = cos.findById(communeId);

        if (client == null || commune == null) {
            redirectAttributes.addFlashAttribute("message", "Client ou commune invalide.");
            return "redirect:/";
        }

        ds.createDemande(client, commune, dateDemande, lieu);
        redirectAttributes.addFlashAttribute("message", "Demande envoyée avec succès.");
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/{id}/update")
    public String updateDemande(
            @PathVariable Long id,
            @RequestParam("clientId") Long clientId,
            @RequestParam("communeId") Long communeId,
            @RequestParam("dateDemande") LocalDate dateDemande,
            @RequestParam("lieu") String lieu,
            RedirectAttributes redirectAttributes) {
        Client client = cs.findById(clientId);
        Commune commune = cos.findById(communeId);

        if (client == null || commune == null) {
            redirectAttributes.addFlashAttribute("message", "Client ou commune invalide.");
            return "redirect:/demandes";
        }

        Demande demande = ds.updateDemande(id, client, commune, dateDemande, lieu);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/demandes";
        }

        redirectAttributes.addFlashAttribute("message", "Demande modifiée avec succès.");
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/{id}/delete")
    public String deleteDemande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ds.deleteDemande(id);
        redirectAttributes.addFlashAttribute("message", "Demande supprimée avec succès.");
        return "redirect:/demandes";
    }

    @GetMapping("/demandes/{id}/devis/nouveau")
    public String nouveauDevis(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Demande demande = ds.getDemandeById(id);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/demandes";
        }

        String statut = demande.getStatutActuelLibelle();
        if (!"valide".equalsIgnoreCase(statut) && !"accepte".equalsIgnoreCase(statut) && !"accepté".equalsIgnoreCase(statut)) {
            redirectAttributes.addFlashAttribute("message", "Le devis ne peut être créé que pour une demande acceptée.");
            return "redirect:/demandes";
        }

        java.util.List<Devis> devisList = dvs.findByDemandeId(id);
        model.addAttribute("demande", demande);
        model.addAttribute("devisList", devisList);
        return "ajout-devis";
    }

    @PostMapping("/demandes/{id}/devis")
    public String enregistrerDevis(
            @PathVariable Long id,
            @RequestParam(name = "objet") List<String> objets,
            @RequestParam(name = "montant") List<Double> montants,
            RedirectAttributes redirectAttributes) {
        Demande demande = ds.getDemandeById(id);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("message", "Demande introuvable.");
            return "redirect:/demandes";
        }

        String statut = demande.getStatutActuelLibelle();
        if (!"valide".equalsIgnoreCase(statut) && !"accepte".equalsIgnoreCase(statut) && !"accepté".equalsIgnoreCase(statut)) {
            redirectAttributes.addFlashAttribute("message", "Le devis ne peut être créé que pour une demande acceptée.");
            return "redirect:/demandes";
        }

        dvs.saveForDemande(demande, objets, montants);
        redirectAttributes.addFlashAttribute("message", "Devis enregistré avec succès.");
        return "redirect:/demandes/" + id + "/devis";
    }

    @GetMapping("/demandes/{id}/devis")
    public String voirDevis(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
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
    public String refuserDemande(@PathVariable Long id) {
        ds.marquerRefuse(id);
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/{id}/valider")
    public String validerDemande(@PathVariable Long id) {
        ds.marquerValide(id);
        return "redirect:/demandes";
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

    

}
