package com.forage.forage.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;

@Entity
public class DemandeDevis {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false, unique = true)
    private Demande demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devis", nullable = false, unique = true)
    private Devis devis;

    public DemandeDevis() {}

    public DemandeDevis(Demande demande, Devis devis) {
        this.demande = demande;
        this.devis = devis;
    }

    public Long getId() {return id;}
    public Demande getDemande() {return demande;}
    public Devis getDevis() {return devis;}
    public void setDemande(Demande demande) {this.demande = demande;}
    public void setDevis(Devis devis) {this.devis = devis;}
}
