package fr.ece.javaprojetfinal.basics;

import fr.ece.javaprojetfinal.PasswordUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * controlleur pour la page des parametres du compte utilisateur
 */
public class ParametresMonComptecontroller implements Initializable {

    @FXML private Label usernameField;
    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField passwordField;
    @FXML private TextField passwordField1;
    @FXML private Button annulerBtn;
    @FXML private Button enregistrerBtn;
    @FXML private Button retourBtn;

    private int userId = -1;
    private DBUtil.User originalUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        annulerBtn.setOnAction(this::onAnnuler);
        enregistrerBtn.setOnAction(this::onEnregistrer);
        retourBtn.setOnAction(this::closeWindow);
    }

    /**
     * Appel pour définir l'ID utilisateur à modifier.
     * @param id l'ID utilisateur
     */
    public void setUserId(int id) {
        this.userId = id;
        loadUser();
    }

    private void loadUser() {
        if (userId < 0) return;
        Optional<DBUtil.User> maybeUser = DBUtil.getUserById(userId);
        if (maybeUser.isPresent()) {
            originalUser = maybeUser.get();
            usernameField.setText(originalUser.name);
            nomField.setText(originalUser.name);
            adresseField.setText(originalUser.address == null ? "" : originalUser.address);
            passwordField.clear();
            passwordField1.clear();
            nomField.setEditable(true);
            adresseField.setEditable(true);
            passwordField.setEditable(true);
            passwordField1.setEditable(true);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les informations utilisateur.");
        }
    }

    private void onAnnuler(ActionEvent e) {
        if (originalUser != null) {
            nomField.setText(originalUser.name);
            adresseField.setText(originalUser.address == null ? "" : originalUser.address);
            passwordField.clear();
            passwordField1.clear();
        }
    }

    private void onEnregistrer(ActionEvent e) {
        if (originalUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune donnée utilisateur à modifier.");
            return;
        }

        String newName = nomField.getText().trim();
        String newAddress = adresseField.getText().trim();
        String p1 = passwordField.getText();
        String p2 = passwordField1.getText();

        if (newName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom ne peut pas être vide.");
            return;
        }

        String newMdp;
        if (!p1.isEmpty() || !p2.isEmpty()) {
            if (!p1.equals(p2)) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Les mots de passe ne correspondent pas.");
                return;
            }
            newMdp = PasswordUtils.hashPassword(p1);
        } else {
            newMdp = originalUser.mdp;
        }

        boolean ok = DBUtil.updateUser(originalUser.id, newName, newAddress.isEmpty() ? null : newAddress, newMdp);
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Informations mises à jour.");
            loadUser();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour les informations.");
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Node n = (Node) event.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
