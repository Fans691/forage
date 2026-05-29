package com.forage.forage.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "region")
public class Region {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "libelle", nullable = false)
	private String libelle;

	@OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<District> districts = new ArrayList<>();

	public Region() {}

	public Region(String libelle) {
		this.libelle = libelle;
	}

	public Long getId() {return id;}
	public String getLibelle() {return libelle;}
	public List<District> getDistricts() {return districts;}

	public void setLibelle(String libelle) {this.libelle = libelle;}
	public void setDistricts(List<District> districts) {this.districts = districts;}
}
