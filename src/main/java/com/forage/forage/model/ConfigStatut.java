package com.forage.forage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "config")
public class ConfigStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id1", nullable = false)
    private Statut statut1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id2", nullable = false)
    private Statut statut2;

    @Column(name = "dt1", nullable = false)
    private Long dt1;

    @Column(name = "dt2", nullable = false)
    private Long dt2;

    @Column(name = "code_couleur", nullable = false)
    private String codeCouleur;

    public ConfigStatut() {}

    public ConfigStatut(Statut statut1, Statut statut2, Long dt1, Long dt2, String codeCouleur) {
        this.statut1 = statut1;
        this.statut2 = statut2;
        this.dt1 = dt1;
        this.dt2 = dt2;
        this.codeCouleur = codeCouleur;
    }

    public Long getId() {return id;}
    public Statut getStatut1() {return statut1;}
    public Statut getStatut2() {return statut2;}
    public Long getDt1() {return dt1;}
    public Long getDt2() {return dt2;}
    public String getCodeCouleur() {return codeCouleur;}

    public void setStatut1(Statut statut1) {this.statut1 = statut1;}
    public void setStatut2(Statut statut2) {this.statut2 = statut2;}
    public void setDt1(Long dt1) {this.dt1 = dt1;}
    public void setDt2(Long dt2) {this.dt2 = dt2;}
    public void setCodeCouleur(String codeCouleur) {this.codeCouleur = codeCouleur;}
}
