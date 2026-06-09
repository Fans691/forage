package com.forage.forage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_statut")
public class DemandeStatut {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_statut", nullable = false)
	private Statut statut;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_demande", nullable = false)
	private Demande demande;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	@Column(name="DT")
	private Double dt;

	@Column(name = "duree_travaille_total")
	private Double dureeTravailleTotal;

	public DemandeStatut() {}

	public DemandeStatut(Statut statut, Demande demande, String description, LocalDateTime date, Double dt) {
		this.statut = statut;
		this.demande = demande;
		this.description = description;
		this.date = date;
		this.dt = dt;
	}

	public Long getId() {return id;}
	public Statut getStatut() {return statut;}
	public Demande getDemande() {return demande;}
	public String getDescription() {return description;}
	public LocalDateTime getDate() {return date;}

	public void setStatut(Statut statut) {this.statut = statut;}
	public void setDemande(Demande demande) {this.demande = demande;}
	public void setDescription(String description) {this.description = description;}
	public void setDate(LocalDateTime date) {this.date = date;}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getDt() {
		return dt;
	}

	public void setDt(Double dt) {
		this.dt = dt;
	}

	public Double getDureeTravailleTotal() {
		return dureeTravailleTotal;
	}

	public void setDureeTravailleTotal(Double dureeTravailleTotal) {
		this.dureeTravailleTotal = dureeTravailleTotal;
	}
}
