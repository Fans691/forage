package com.forage.forage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devis")
public class Devis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande demande;

    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "qte", nullable = true)
    private Double qte;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_devis")
    private LocalDateTime dateDevis;

    public Devis() {}

    public Devis(Demande demande, Double montant, Double qte, String description, LocalDateTime dateDevis) {
        this.demande = demande;
        this.montant = montant;
        this.qte = qte;
        this.description = description;
        this.dateDevis = dateDevis;
    }

    public Long getId() {return id;}
    public Demande getDemande() {return demande;}
    public Double getMontant() {return montant;}
    public Double getQte() {return qte;}
    public String getDescription() {return description;}
    public LocalDateTime getDateDevis() {return dateDevis;}

    public void setDemande(Demande demande) {this.demande = demande;}
    public void setMontant(Double montant) {this.montant = montant;}
    public void setQte(Double qte) {this.qte = qte;}
    public void setDescription(String description) {this.description = description;}
    public void setDateDevis(LocalDateTime dateDevis) {this.dateDevis = dateDevis;}
}
