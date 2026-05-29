package com.forage.forage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devis_statut")
public class DevisStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut", nullable = false)
    private Statut statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devis", nullable = false)
    private Devis devis;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public DevisStatut() {}

    public DevisStatut(Statut statut, Devis devis, String description, LocalDateTime date) {
        this.statut = statut;
        this.devis = devis;
        this.description = description;
        this.date = date;
    }

    public Long getId() { return id; }
    public Statut getStatut() { return statut; }
    public Devis getDevis() { return devis; }
    public String getDescription() { return description; }
    public LocalDateTime getDate() { return date; }

    public void setStatut(Statut statut) { this.statut = statut; }
    public void setDevis(Devis devis) { this.devis = devis; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
