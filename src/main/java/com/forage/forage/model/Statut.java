package com.forage.forage.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "statut")
public class Statut {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "libelle", nullable = false)
	private String libelle;

	@OneToMany(mappedBy = "statut")
	private List<DemandeStatut> demandeStatuts = new ArrayList<>();

	@OneToMany(mappedBy = "statut")
	private List<DevisStatut> devisStatuts = new ArrayList<>();

	public Statut() {}

	public Statut(String libelle) {
		this.libelle = libelle;
	}

	public Long getId() {return id;}
	public String getLibelle() {return libelle;}
	public List<DevisStatut> getDevisStatuts() {return devisStatuts;}
	public List<DemandeStatut> getDemandeStatuts() {return demandeStatuts;}

	public void setLibelle(String libelle) {this.libelle = libelle;}
	public void setDevisStatuts(List<DevisStatut> devisStatuts) {this.devisStatuts = devisStatuts;}
	public void setDemandeStatuts(List<DemandeStatut> demandeStatuts) {this.demandeStatuts = demandeStatuts;}
}
