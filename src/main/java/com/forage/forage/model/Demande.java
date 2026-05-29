package com.forage.forage.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "demande")
public class Demande {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_client", nullable = false)
	private Client client;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commune", nullable = false)
	private Commune commune;

	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(name = "date_demande", nullable = false)
	private LocalDate dateDemande;

	@Column(name = "lieu", nullable = false)
	private String lieu;

	@OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DemandeStatut> demandeStatuts = new ArrayList<>();

	public Demande() {}

	public Demande(Client client, Commune commune, String description, LocalDate dateDemande, String lieu) {
		this.client = client;
		this.commune = commune;
		this.description = description;
		this.dateDemande = dateDemande;
		this.lieu = lieu;
	}

	public Long getId() {return id;}
	public Client getClient() {return client;}
	public Commune getCommune() {return commune;}
	public String getDescription() {return description;}
	public LocalDate getDateDemande() {return dateDemande;}
	public String getLieu() {return lieu;}
	public List<DemandeStatut> getDemandeStatuts() {return demandeStatuts;}
	@Transient
	public DemandeStatut getStatutActuel() {
		return demandeStatuts.stream()
				.max(Comparator.comparing(DemandeStatut::getDate))
				.orElse(null);
	}

	@Transient
	public String getStatutActuelLibelle() {
		DemandeStatut statutActuel = getStatutActuel();
		return statutActuel != null && statutActuel.getStatut() != null ? statutActuel.getStatut().getLibelle() : "";
	}

	public void setClient(Client client) {this.client = client;}
	public void setCommune(Commune commune) {this.commune = commune;}
	public void setDescription(String description) {this.description = description;}
	public void setDateDemande(LocalDate dateDemande) {this.dateDemande = dateDemande;}
	public void setLieu(String lieu) {this.lieu = lieu;}
	public void setDemandeStatuts(List<DemandeStatut> demandeStatuts) {this.demandeStatuts = demandeStatuts;}
}
