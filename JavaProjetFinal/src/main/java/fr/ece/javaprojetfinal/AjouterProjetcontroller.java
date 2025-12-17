package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AjouterProjetcontroller extends BaseController {

    @FXML private TextField nomProjetField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateEcheancePicker;
    @FXML private ComboBox<String> responsableCombo; // si tu veux l'utiliser plus tard
    @FXML private ComboBox<String> statutCombo;      // idem
    @FXML private Button retourBtn;
    @FXML private Button annulerBtn;
    @FXML private Button enregistrerBtn;

    private Scene previousScene;
    private HomeProjetAdmincontroller parentController;
    private final ProjetDAO projetDAO = new ProjetDAO();

    @FXML
    private void initialize() {
        // Initialisation session + contrôle d'accès
        initializeSession();
        if (!checkPagePermissions()) {
            showErrorAndExit("Accès refusé.");
            return;
        }

        // Le champ nom est éditable pour la création
        if (nomProjetField != null) {
            nomProjetField.setEditable(true);
        }

        // Boutons
        if (retourBtn != null) {
            retourBtn.setOnAction(e -> returnToPrevious());
        }
        if (annulerBtn != null) {
            annulerBtn.setOnAction(e -> resetForm());
        }
        if (enregistrerBtn != null) {
            enregistrerBtn.setOnAction(e -> saveProject());
        }

        // Initialisation simple du statut si le ComboBox est présent
        if (statutCombo != null && statutCombo.getItems().isEmpty()) {
            statutCombo.getItems().addAll("En cours", "Terminé");
            statutCombo.getSelectionModel().select("En cours");
        }
    }

    // =========================
    // SÉCURITÉ
    // =========================
    @Override
    protected boolean checkPagePermissions() {
        // page réservée à l'admin
        return getSession().isAdmin();
    }

    // =========================
    // CONFIG PARENT / NAVIGATION
    // =========================
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setParentController(HomeProjetAdmincontroller controller) {
        this.parentController = controller;
    }

    private void returnToPrevious() {
        Stage stage = (Stage) enregistrerBtn.getScene().getWindow();
        if (previousScene != null) {
            stage.setScene(previousScene);
        } else {
            stage.close();
        }
    }

    // =========================
    // LOGIQUE FORMULAIRE
    // =========================
    private void resetForm() {
        if (nomProjetField != null) nomProjetField.clear();
        if (descriptionArea != null) descriptionArea.clear();
        if (dateEcheancePicker != null) dateEcheancePicker.setValue(null);
        if (statutCombo != null) statutCombo.getSelectionModel().select("En cours");
    }

    private void saveProject() {
        String nom = nomProjetField != null ? nomProjetField.getText() : null;
        String description = descriptionArea != null ? descriptionArea.getText() : null;
        LocalDate ld = dateEcheancePicker != null ? dateEcheancePicker.getValue() : null;

        if (nom == null || nom.isBlank()) {
            showError("Le nom du projet est requis.");
            return;
        }

        Date dateEcheance = null;
        if (ld != null) {
            dateEcheance = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        Date dateCreation = new Date();
        Integer responsableId = getCurrentUserId();
        String statut = (statutCombo != null && statutCombo.getValue() != null)
                ? statutCombo.getValue()
                : "En cours";

        Projet projet = new Projet(0, nom, description, dateCreation, dateEcheance, responsableId, statut);

        try {
            projetDAO.insertNewProjet(projet);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText(null);
            ok.setContentText("Projet créé avec succès.");
            ok.showAndWait();




            returnToPrevious();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erreur lors de la création du projet.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
