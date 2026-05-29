package com.forage.forage.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "district")
public class District {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "libelle", nullable = false)
	private String libelle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_region", nullable = false)
	private Region region;

	@OneToMany(mappedBy = "district", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Commune> communes = new ArrayList<>();

	public District() {}

	public District(String libelle, Region region) {
		this.libelle = libelle;
		this.region = region;
	}

	public Long getId() {return id;}
	public String getLibelle() {return libelle;}
	public Region getRegion() {return region;}
	public List<Commune> getCommunes() {return communes;}

	public void setLibelle(String libelle) {this.libelle = libelle;}
	public void setRegion(Region region) {this.region = region;}
	public void setCommunes(List<Commune> communes) {this.communes = communes;}
}
