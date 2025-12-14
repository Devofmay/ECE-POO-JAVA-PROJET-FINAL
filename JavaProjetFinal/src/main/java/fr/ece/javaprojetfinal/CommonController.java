
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

        if (username.isEmpty()) {
            errorMsg.setText("Le nom d'utilisateur est obligatoire");
            return;
        }

        String query = "SELECT * FROM utilisateur WHERE Name = ?";

        try (Connection conn = DBconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("MDP");
                    int storedRole = rs.getInt("Role");
                    if (storedPassword != null && PasswordUtils.verifyPassword(password, storedPassword)) {
                        errorMsg.setText("Login successful");
                        int userid = rs.getInt("ID");
                        if (storedRole == 1) {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeprojetsAdmin.fxml"));
                            Parent root = loader.load();
                            HomeProjetAdmincontroller adminController = loader.getController();
                            adminController.setLoggedInUserId(userid);
                            adminController.setUser(userid, username, true);

                            Scene scene = new Scene(root);
                            Stage stage = (Stage) usernameField.getScene().getWindow();
                            stage.setTitle("Bienvenue " + username);
                            stage.setScene(scene);
                            stage.show();
                        } else if (storedRole == 0) {
                            // Normal user: open HomeUsertaches.fxml (correct name & path)
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/HomeUsertaches.fxml"));
                            Parent root;
                            try {
                                root = loader.load();
                            } catch (IOException ex) {
                                errorMsg.setText("Impossible d'ouvrir la vue utilisateur");
                                ex.printStackTrace();
                                return;
                            }

                            HomeUserTacheController userController = loader.getController();
                            userController.setUser(userid, username);

                            Scene scene = new Scene(root);
                            Stage stage = (Stage) usernameField.getScene().getWindow();
                            stage.setTitle("Tâches de " + username);
                            stage.setScene(scene);
                            stage.show();
                        }
                    } else {
                        errorMsg.setText("Mot de passe incorrect");
                    }
                } else {
                    errorMsg.setText("Utilisateur introuvable");
                }
            }
        } catch (SQLException e) {
            errorMsg.setText("Erreur de base de données");
            e.printStackTrace();
        }
    }
}
