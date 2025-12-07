package main.java.fr.ece.javaprojetfinal.basics;

import java.util.Date;

public class Taches {
    private int id;
    private String nom;
    private String description;
    private Date dataCreation;
    private Date dateEcheances;
    private int idProjet;
    private String statut;
    private String priorite;

    public Taches(int id, String nom, String description, Date dataCreation, Date dateEcheances, int idProjet, String statut, String priorite) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dataCreation = dataCreation;
        this.dateEcheances = dateEcheances;
        this.idProjet = idProjet;
        this.statut = statut;
        this.priorite = priorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDataCreation() {
        return dataCreation;
    }

    public void setDataCreation(Date dataCreation) {
        this.dataCreation = dataCreation;
    }

    public Date getDateEcheances() {
        return dateEcheances;
    }

    public void setDateEcheances(Date dateEcheances) {
        this.dateEcheances = dateEcheances;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }
}