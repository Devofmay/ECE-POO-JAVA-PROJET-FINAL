package main.java.fr.ece.javaprojetfinal.basics;

public class UtilisateursProjet {
    private int id;
    private int userId;
    private int projetId;

    public UtilisateursProjet(int id, int userId, int projetId) {
        this.id = id;
        this.userId = userId;
        this.projetId = projetId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProjetId() {
        return projetId;
    }

    public void setProjetId(int projetId) {
        this.projetId = projetId;
    }
}
