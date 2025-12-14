package fr.ece.javaprojetfinal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.application.Platform;

public abstract class BaseController {

    @FXML
    protected Label usernameSpot;

    @FXML
    protected Label usernameField;

    @FXML
    protected Label roleLabel;

    // Initialisation de la session (à appeler dans initialize() des enfants)
    public void initializeSession() {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            showErrorAndExit("Session expirée. Veuillez vous reconnecter.");
            return;
        }

        // Vérification des permissions selon le type de page
        if (!checkPagePermissions()) {
            showErrorAndExit("Vous n'avez pas accès à cette page.");
            return;
        }

        // Affichage des informations utilisateur
        String username = session.getUsername();
        if (usernameSpot != null) {
            usernameSpot.setText(username);
        }
        if (usernameField != null) {
            usernameField.setText(username);
        }
        if (roleLabel != null) {
            roleLabel.setText(session.getRoleString());
        }

        System.out.println("✓ Session initialisée pour: " + username + " | Rôle: " + session.getRoleString());
    }

    // Méthode abstraite : chaque page définit ses permissions
    protected abstract boolean checkPagePermissions();

    // Déconnexion (à lier à un bouton via FXML)
    @FXML
    protected void handleLogout(ActionEvent event) {
        System.out.println("→ Demande de déconnexion");
        SessionManager.logout(event);
    }

    // Afficher une erreur et fermer/quitter
    protected void showErrorAndExit(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur d'accès");
        alert.setHeaderText("Accès non autorisé");
        alert.setContentText(message);
        alert.showAndWait();

        Platform.exit();
    }

    // Getters utilitaires
    protected Session getSession() {
        return Session.getInstance();
    }

    protected int getCurrentUserId() {
        return Session.getInstance().getUserId();
    }

    protected String getCurrentUsername() {
        return Session.getInstance().getUsername();
    }

    protected boolean isCurrentUserAdmin() {
        return Session.getInstance().isAdmin();
    }

    protected boolean isCurrentUserRegularUser() {
        return Session.getInstance().isUser();
    }

    // Vérifier si l'action nécessite des droits admin
    protected boolean requiresAdminAccess(ActionEvent event) {
        if (!SessionManager.checkAdminAccess()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Accès refusé");
            alert.setHeaderText("Droits insuffisants");
            alert.setContentText("Cette action nécessite des droits d'administrateur.");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}