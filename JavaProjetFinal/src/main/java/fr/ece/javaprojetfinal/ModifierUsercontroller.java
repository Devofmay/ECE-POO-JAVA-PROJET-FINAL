package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Utilisateur;
import fr.ece.javaprojetfinal.basics.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ModifierUsercontroller extends BaseController {

    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button annulerBtn;
    @FXML private Button enregistrerBtn;
    @FXML private Button retourBtn;

    private Utilisateur user;
    private final UtilisateurDAO dao = new UtilisateurDAO();

    // ======================
    // INITIALISATION
    // ======================
    @FXML
    private void initialize() {
        initializeSession();

        if (!checkPagePermissions()) {
            showErrorAndExit("Accès refusé.");
            return;
        }

        roleCombo.getItems().setAll("Admin", "User");

        annulerBtn.setOnAction(e -> closeWindow());
        retourBtn.setOnAction(e -> closeWindow());
        enregistrerBtn.setOnAction(e -> saveAndClose());
    }

    // ======================
    // SÉCURITÉ
    // ======================
    @Override
    protected boolean checkPagePermissions() {
        return getSession().isAdmin();
    }

    // ======================
    // DATA BINDING
    // ======================
    public void setUser(Utilisateur u) {
        this.user = u;
        if (u == null) return;

        nomField.setText(u.getNom());
        adresseField.setText(u.getAdresse());
        roleCombo.getSelectionModel().select(u.getRole());

        // jamais afficher un mot de passe
        passwordField.clear();
    }

    // ======================
    // SAVE
    // ======================
    private void saveAndClose() {
        if (user == null) {
            closeWindow();
            return;
        }

        if (nomField.getText() == null || nomField.getText().isBlank()) {
            showError("Le nom est obligatoire.");
            return;
        }

        user.setNom(nomField.getText());
        user.setAdresse(adresseField.getText());
        user.setRole(roleCombo.getValue());

        // modifier le mot de passe uniquement s'il est saisi
        if (!passwordField.getText().isBlank()) {
            user.setMotDePasse(passwordField.getText());
        }

        try {
            dao.update(user);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText(null);
            ok.setContentText("Utilisateur modifié avec succès.");
            ok.showAndWait();

            closeWindow();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erreur lors de l'enregistrement.");
        }
    }

    // ======================
    // UTILS
    // ======================
    private void closeWindow() {
        Stage stage = (Stage) enregistrerBtn.getScene().getWindow();
        stage.close();
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
