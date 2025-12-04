package main.java.fr.ece.javaprojetfinal.basics;

import java.util.Date;

public class Commentaires {
    private int id;
    private int userId;
    private int tacheId;
    private String description;
    private Date dateCreation;

    public Commentaires(int id, int userId, int tacheId, String description, Date dateCreation) {
        this.id = id;
        this.userId = userId;
        this.tacheId = tacheId;
        this.description = description;
        this.dateCreation = dateCreation;
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

    public int getTacheId() {
        return tacheId;
    }

    public void setTacheId(int tacheId) {
        this.tacheId = tacheId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}
