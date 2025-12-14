package fr.ece.javaprojetfinal.basics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Tache {
    private int id;
    private String nom;
    private String description;
    private LocalDate dateCreation;
    private LocalDate dateEcheance;
    private int projetId;
    private String projetNom;
    private String statut;
    private String priorite;
    private String ownerName;

    public Tache() {}

    public Tache(int id, String nom, String desc, Date dateCreation, Date dateEcheance, int idProjet, String statut, String priorite) {
        this.id = id;
        this.nom = nom;
        this.description = desc;
        // Convert java.util.Date to LocalDate
        if (dateCreation != null) {
            this.dateCreation = dateCreation.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (dateEcheance != null) {
            this.dateEcheance = dateEcheance.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        this.projetId = idProjet;
        this.statut = statut;
        this.priorite = priorite;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public int getProjetId() { return projetId; }
    public void setProjetId(int projetId) { this.projetId = projetId; }
    public String getProjetNom() { return projetNom; }
    public void setProjetNom(String projetNom) { this.projetNom = projetNom; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }


    public Date getDateEcheances() {
        if (this.dateEcheance == null) return null;
        return java.sql.Date.valueOf(this.dateEcheance);
    }

    public void setDateEcheances(Date d) {
        if (d == null) {
            this.dateEcheance = null;
        } else {
            this.dateEcheance = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    public String getResponsable() {
        return ownerName;
    }

    public void setResponsable(String responsable) {
        this.ownerName = responsable;
    }

}
