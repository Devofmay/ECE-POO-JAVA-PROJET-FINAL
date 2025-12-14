package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Utilisateur;
import fr.ece.javaprojetfinal.basics.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AjouterUsercontroller extends BaseController {

    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button annulerBtn;
    @FXML private Button enregistrerBtn;
    @FXML private Button retourBtn;

    private final UtilisateurDAO dao = new UtilisateurDAO();

    @FXML
    private void initialize() {
        initializeSession();

        if (roleCombo != null) {
            roleCombo.getItems().setAll("Admin", "User");
            roleCombo.getSelectionModel().selectFirst();
        }

        if (annulerBtn != null) annulerBtn.setOnAction(e -> closeWindow());
        if (retourBtn != null) retourBtn.setOnAction(e -> closeWindow());
        if (enregistrerBtn != null) enregistrerBtn.setOnAction(e -> saveUser());
    }

    @Override
    protected boolean checkPagePermissions() {
        return getSession().isAdmin();
    }

    private void saveUser() {
        String nom = nomField.getText();
        String adresse = adresseField.getText();
        String password = passwordField.getText();
        String role = roleCombo.getSelectionModel().getSelectedItem();

        if (nom == null || nom.trim().isEmpty()) {
            showError("Le nom ne peut pas être vide.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showError("Le mot de passe ne peut pas être vide.");
            return;
        }

        Utilisateur newUser = new Utilisateur(0, nom, adresse, role, password);

        try {
            dao.insert(newUser);
            Alert success = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Collaborateur ajouté avec succès.",
                    ButtonType.OK
            );
            success.showAndWait();
            closeWindow();
        } catch (SQLException ex) {
            showError("Erreur lors de l'ajout.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = null;

        if (enregistrerBtn != null && enregistrerBtn.getScene() != null) {
            stage = (Stage) enregistrerBtn.getScene().getWindow();
        } else if (annulerBtn != null && annulerBtn.getScene() != null) {
            stage = (Stage) annulerBtn.getScene().getWindow();
        }

        if (stage != null) stage.close();
    }
}
