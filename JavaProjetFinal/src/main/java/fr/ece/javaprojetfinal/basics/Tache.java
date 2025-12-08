package fr.ece.javaprojetfinal.basics;

import java.util.Date;

public class Tache {
    private final int id;
    private final String nom;
    private final String description;
    private final Date dateCreation;
    private final Date dateEcheances;
    private final int idProjet;
    private final String statut;
    private final String priorite;
    private String ownerName; // optional display field

    public Tache(int id, String nom, String description, Date dateCreation, Date dateEcheances, int idProjet, String statut, String priorite) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateEcheances = dateEcheances;
        this.idProjet = idProjet;
        this.statut = statut;
        this.priorite = priorite;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public Date getDateCreation() { return dateCreation; }
    public Date getDateEcheances() { return dateEcheances; }
    public int getIdProjet() { return idProjet; }
    public String getStatut() { return statut; }
    public String getPriorite() { return priorite; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
