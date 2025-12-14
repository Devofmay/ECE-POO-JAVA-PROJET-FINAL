package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Utilisateur;
import fr.ece.javaprojetfinal.basics.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import static fr.ece.javaprojetfinal.PasswordUtils.hashPassword;

public class  ModifierUsercontroller {
    @FXML
    private TextField nomField;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField passwordField;
    @FXML
    private ComboBox<String> roleCombo;
    @FXML
    private Button annulerBtn;
    @FXML
    private Button enregistrerBtn;
    @FXML
    private Button retourBtn;

    private Utilisateur user;
    private final UtilisateurDAO dao = new UtilisateurDAO();

    @FXML
    private void initialize() {
        if (roleCombo != null) {
            roleCombo.getItems().setAll("Admin", "User");
        }
        // wire cancel/return to close window
        if (annulerBtn != null) annulerBtn.setOnAction(e -> closeWindow());
        if (retourBtn != null) retourBtn.setOnAction(e -> closeWindow());
        if (enregistrerBtn != null) enregistrerBtn.setOnAction(e -> saveAndClose());
    }

    public void setUser(Utilisateur u) {
        this.user = u;
        if (u == null) return;
        if (nomField != null) nomField.setText(u.getNom());
        if (adresseField != null) adresseField.setText(u.getAdresse());
        if (passwordField != null) passwordField.setText(u.getMotDePasse());
        if (roleCombo != null) {
            String role = u.getRole() == null ? "" : u.getRole();
            if (!roleCombo.getItems().contains(role)) {
                // ensure selection even if DB uses other values
                roleCombo.getItems().add(role);
            }
            roleCombo.getSelectionModel().select(role);
        }
    }

    private void saveAndClose() {
        if (user == null) {
            closeWindow();
            return;
        }
        // update model from fields
        if (nomField != null) user.setNom(nomField.getText());
        if (adresseField != null) user.setAdresse(adresseField.getText());
        if (passwordField != null) user.setMotDePasse(hashPassword(passwordField.getText()));
        if (roleCombo != null) user.setRole(roleCombo.getSelectionModel().getSelectedItem());

        try {
            dao.update(user);
            closeWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR, "Failed to save user: " + ex.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }

    private void closeWindow() {
        Stage s = null;
        if (enregistrerBtn != null && enregistrerBtn.getScene() != null) {
            s = (Stage) enregistrerBtn.getScene().getWindow();
        } else if (annulerBtn != null && annulerBtn.getScene() != null) {
            s = (Stage) annulerBtn.getScene().getWindow();
        }
        if (s != null) s.close();
    }
}
