package fr.ece.javaprojetfinal.basics;

public class Utilisateur {
    private int id;
    private String nom;
    private String adresse;
    private String role;
    private String motDePasse;

    public Utilisateur(int id, String nom, String adresse, String role, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.role = role;
        this.motDePasse = motDePasse;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getAdresse() { return adresse; }
    public String getRole() { return role; }
    public String getMotDePasse() { return motDePasse; }

    public void setNom(String nom) { this.nom = nom; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setRole(String role) { this.role = role; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
