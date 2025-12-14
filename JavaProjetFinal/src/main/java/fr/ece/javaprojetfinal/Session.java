package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Utilisateur;

public class Session {
    private static Session instance;

    private Utilisateur currentUser;
    private boolean isAdmin;
    private int userId;
    private String username;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // Méthode de connexion simplifiée (utilisée par CommonController)
    public void login(int userId, String username, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.isAdmin = isAdmin;
        System.out.println("✓ Session créée - User: " + username + " | Admin: " + isAdmin + " | ID: " + userId);
    }

    // Méthode de connexion avec objet Utilisateur (optionnelle)
    public void login(Utilisateur user, boolean admin) {
        this.currentUser = user;
        this.userId = user.getId();
        this.username = user.getNom();
        this.isAdmin = admin;
        System.out.println("✓ Session créée - User: " + username + " | Admin: " + admin + " | ID: " + userId);
    }

    public void logout() {
        System.out.println("✗ Déconnexion - User: " + username);
        this.currentUser = null;
        this.userId = -1;
        this.username = null;
        this.isAdmin = false;
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return userId != -1 && username != null;
    }

    public boolean isAdmin() {
        return isAdmin && isLoggedIn();
    }

    public boolean isUser() {
        return !isAdmin && isLoggedIn();
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username != null ? username : "Invité";
    }

    // Méthodes de vérification
    public boolean hasAdminAccess() {
        return isLoggedIn() && isAdmin;
    }

    public boolean hasUserAccess() {
        return isLoggedIn() && !isAdmin;
    }

    public String getRoleString() {
        if (!isLoggedIn()) return "Non connecté";
        return isAdmin ? "Administrateur" : "Utilisateur";
    }
}