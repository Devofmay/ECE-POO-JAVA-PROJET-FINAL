package fr.ece.javaprojetfinal;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class SessionManager {

    // Déconnexion complète avec redirection vers login
    public static void logout(ActionEvent event) {
        Session.getInstance().logout();
        navigateToLogin(event);
    }

    // Navigation vers login (utilisé lors de la déconnexion ou accès refusé)
    public static void navigateToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(SessionManager.class.getResource("login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion - Project Manager");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la page de connexion");
        }
    }

    // Navigation vers la page admin avec vérification stricte
    public static void navigateToAdminHome(ActionEvent event) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            showAccessDenied("Vous devez être connecté");
            navigateToLogin(event);
            return;
        }

        if (!session.isAdmin()) {
            showAccessDenied("Accès réservé aux administrateurs");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(SessionManager.class.getResource("HomeprojetsAdmin.fxml"));
            Parent root = loader.load();

            HomeProjetAdmincontroller controller = loader.getController();
            controller.initializeSession();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Administration - " + session.getUsername());
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la page administrateur");
        }
    }

    // Navigation vers la page utilisateur avec vérification stricte
    public static void navigateToUserHome(ActionEvent event) {
        Session session = Session.getInstance();

        if (!session.isLoggedIn()) {
            showAccessDenied("Vous devez être connecté");
            navigateToLogin(event);
            return;
        }

        if (session.isAdmin()) {
            showAccessDenied("Cette page est réservée aux utilisateurs standards");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(SessionManager.class.getResource("/fr/ece/javaprojetfinal/HomeUsertaches.fxml"));
            Parent root = loader.load();

            HomeUserTacheController controller = loader.getController();
            controller.initializeSession();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Mes Tâches - " + session.getUsername());
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la page utilisateur");
        }
    }

    // Vérifications de session
    public static boolean checkSession() {
        return Session.getInstance().isLoggedIn();
    }

    public static boolean checkAdminAccess() {
        return Session.getInstance().hasAdminAccess();
    }

    public static boolean checkUserAccess() {
        return Session.getInstance().hasUserAccess();
    }

    // Afficher une alerte d'accès refusé
    private static void showAccessDenied(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Accès refusé");
        alert.setHeaderText("Permissions insuffisantes");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Afficher une erreur générique
    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}