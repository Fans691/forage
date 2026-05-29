package com.forage.forage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "mdp", nullable = false)
    private String mdp;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "contact")
    private String contact;

    public Client() {}
    public Client(String nom, String adresse, String contact, String mdp) {
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
        this.mdp = mdp;
    }

    public Long getId() {return this.id;}
    public String getNom() {return this.nom;}
    public String getAdresse() {return this.adresse;}
    public String getContact() {return this.contact;}
    
    public void setNom(String nom) {this.nom = nom;}
    public void setAdresse(String adresse) {this.adresse = adresse;}
    public void setContact(String contact) {this.contact = contact;}


}
