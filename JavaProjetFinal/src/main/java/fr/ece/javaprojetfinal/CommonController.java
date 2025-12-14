package fr.ece.javaprojetfinal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import fr.ece.javaprojetfinal.basics.DBconnect;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommonController {
    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Label errorMsg;

    public void login() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation des entrées
        if (username.isEmpty()) {
            showError("Le nom d'utilisateur est obligatoire");
            return;
        }

        if (password.isEmpty()) {
            showError("Le mot de passe est obligatoire");
            return;
        }

        // Requête SQL sécurisée
        String query = "SELECT ID, Name, MDP, Role FROM utilisateur WHERE Name = ?";

        try (Connection conn = DBconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("MDP");
                    int storedRole = rs.getInt("Role");
                    int userid = rs.getInt("ID");
                    String userName = rs.getString("Name");

                    // Vérification du mot de passe (en clair pour le moment)
                    if (storedPassword != null && storedPassword.equals(password)) {

                        // Déterminer si c'est un admin (Role = 1)
                        boolean isAdmin = (storedRole == 1);

                        // ✅ CRÉER LA SESSION
                        Session.getInstance().login(userid, userName, isAdmin);

                        showSuccess("Connexion réussie");

                        // Redirection selon le rôle
                        if (isAdmin) {
                            System.out.println("→ Redirection vers page Admin");
                            navigateToAdminPage();
                        } else {
                            System.out.println("→ Redirection vers page Utilisateur");
                            navigateToUserPage();
                        }
                    } else {
                        showError("Mot de passe incorrect");
                    }
                } else {
                    showError("Utilisateur introuvable");
                }
            }
        } catch (SQLException e) {
            showError("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    private void navigateToAdminPage() throws IOException {
        Session session = Session.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeprojetsAdmin.fxml"));
        Parent root = loader.load();

        HomeProjetAdmincontroller adminController = loader.getController();
        adminController.initializeSession();

        Scene scene = new Scene(root);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Administration - " + session.getUsername());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void navigateToUserPage() throws IOException {
        Session session = Session.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/HomeUsertaches.fxml"));
        Parent root = loader.load();

        HomeUserTacheController userController = loader.getController();
        userController.initializeSession();

        Scene scene = new Scene(root);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Mes Tâches - " + session.getUsername());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void showError(String message) {
        if (errorMsg != null) {
            errorMsg.setStyle("-fx-text-fill: #ef4444;");
            errorMsg.setText(" " + message);
        }
    }

    private void showSuccess(String message) {
        if (errorMsg != null) {
            errorMsg.setStyle("-fx-text-fill: #10b981;");
            errorMsg.setText("✓ " + message);
        }
    }
}