
package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.Tache;
import fr.ece.javaprojetfinal.basics.TacheDAO;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ModifierTachecontroller extends BaseController {

    @FXML private TextField nomProjetField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateEcheancePicker;
    @FXML private ComboBox<String> responsableCombo;
    @FXML private ComboBox<String> statutCombo1;
    @FXML private ComboBox<String> statutCombo11; // Priorité
    @FXML private Button retourBtn;
    @FXML private Button annulerBtn;
    @FXML private Button enregistrerBtn;

    private Tache currentTache;
    private Scene previousScene;
    private InsideProjetAdminController parentController;

    /** label -> userId */
    private final Map<String, Integer> userLabelToId = new HashMap<>();

    // =========================
    // INITIALISATION
    // =========================
    @FXML
    private void initialize() {
        initializeSession();

        // Sécurité
        if (!checkPagePermissions()) {
            showErrorAndExit("Accès refusé.");
            return;
        }

        // Statut
        statutCombo1.getItems().setAll("À faire", "En cours", "Terminé");

        // Priorité
        statutCombo11.getItems().setAll("Basse", "Moyenne", "Haute");

        // Chargement des utilisateurs
        try {
            ProjetDAO dao = new ProjetDAO();
            Map<Integer, String> users = dao.findAllUsers();
            for (Map.Entry<Integer, String> e : users.entrySet()) {
                String label = e.getValue(); // nom affiché
                userLabelToId.put(label, e.getKey());
            }
            responsableCombo.getItems().setAll(userLabelToId.keySet());
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Impossible de charger les utilisateurs.");
        }

        // Handlers des boutons
        retourBtn.setOnAction(e -> returnToPrevious());
        annulerBtn.setOnAction(e -> resetForm());
        enregistrerBtn.setOnAction(e -> saveTask());
    }

    /** Retour à la scène précédente ou fermeture de la fenêtre */
    public void returnToPrevious() {
        Stage stage = (Stage) enregistrerBtn.getScene().getWindow();
        if (previousScene != null) {
            stage.setScene(previousScene);
        } else {
            stage.close();
        }
    }

    // =========================
    // SÉCURITÉ
    // =========================
    @Override
    protected boolean checkPagePermissions() {
        return getSession().isAdmin();
    }

    // =========================
    // DATA BINDING
    // =========================
    public void setTask(Tache tache) {
        this.currentTache = tache;
        if (tache == null) return;

        nomProjetField.setText(tache.getNom());
        descriptionArea.setText(tache.getDescription());
        dateEcheancePicker.setValue(tache.getDateEcheance());

        if (tache.getStatut() != null) {
            statutCombo1.getSelectionModel().select(tache.getStatut());
        }
        if (tache.getPriorite() != null) {
            statutCombo11.getSelectionModel().select(tache.getPriorite());
        }
        if (tache.getOwnerName() != null) {
            responsableCombo.getSelectionModel().select(tache.getOwnerName());
        }
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setParentController(InsideProjetAdminController controller) {
        this.parentController = controller;
    }

    // =========================
    // ACTIONS
    // =========================
    private void resetForm() {
        setTask(currentTache);
    }

    private void saveTask() {
        if (currentTache == null) return;

        currentTache.setDescription(descriptionArea.getText());
        currentTache.setDateEcheance(dateEcheancePicker.getValue());
        currentTache.setStatut(statutCombo1.getValue());
        currentTache.setPriorite(statutCombo11.getValue());

        String responsableLabel = responsableCombo.getValue();
        if (responsableLabel != null) {
            currentTache.setOwnerName(responsableLabel);
        }

        try {
            TacheDAO dao = new TacheDAO();
            dao.update(currentTache); // à implémenter dans TacheDAO

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText(null);
            ok.setContentText("Tâche mise à jour avec succès.");
            ok.showAndWait();

            if (parentController != null) {
                parentController.setProject(parentController.getCurrentProjet());
            }

            returnToPrevious();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erreur lors de la mise à jour de la tâche.");
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
