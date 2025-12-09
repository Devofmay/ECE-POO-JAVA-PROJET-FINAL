package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.Tache;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class ModifierTachecontroller {

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
    private Map<Integer, String> usersMap;

    @FXML
    private void initialize() {
        // Populate status and priority dropdowns
        if (statutCombo1 != null) {
            statutCombo1.getItems().addAll("À faire", "En cours", "Terminé");
        }
        if (statutCombo11 != null) {
            statutCombo11.getItems().addAll("Basse", "Moyenne", "Haute");
        }

        // Load users for responsable combo
        try {
            ProjetDAO dao = new ProjetDAO();
            usersMap = dao.findAllUsers();
            if (responsableCombo != null) {
                responsableCombo.getItems().addAll(usersMap.values());
            }
        } catch (SQLException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }

        // Button handlers
        if (retourBtn != null) {
            retourBtn.setOnAction(e -> returnToPrevious());
        }
        if (annulerBtn != null) {
            annulerBtn.setOnAction(e -> returnToPrevious());
        }
        if (enregistrerBtn != null) {
            enregistrerBtn.setOnAction(e -> saveTask());
        }
    }

    public void setTask(Tache tache) {
        this.currentTache = tache;
        if (tache == null) return;

        if (nomProjetField != null) {
            nomProjetField.setText(tache.getNom());
        }
        if (descriptionArea != null) {
            descriptionArea.setText(tache.getDescription());
        }
        if (dateEcheancePicker != null && tache.getDateEcheance() != null) {
            dateEcheancePicker.setValue(tache.getDateEcheance());
        }
        if (statutCombo1 != null && tache.getStatut() != null) {
            statutCombo1.setValue(tache.getStatut());
        }
        if (statutCombo11 != null && tache.getPriorite() != null) {
            statutCombo11.setValue(tache.getPriorite());
        }
        if (responsableCombo != null && tache.getOwnerName() != null) {
            responsableCombo.setValue(tache.getOwnerName());
        }
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setParentController(InsideProjetAdminController controller) {
        this.parentController = controller;
    }

    private void saveTask() {
        if (currentTache == null) return;

        // Update task fields
        if (descriptionArea != null) {
            currentTache.setDescription(descriptionArea.getText());
        }
        if (dateEcheancePicker != null) {
            currentTache.setDateEcheance(dateEcheancePicker.getValue());
        }
        if (statutCombo1 != null) {
            currentTache.setStatut(statutCombo1.getValue());
        }
        if (statutCombo11 != null) {
            currentTache.setPriorite(statutCombo11.getValue());
        }

        // TODO: Save to database using TacheDAO
        System.out.println("Task updated: " + currentTache.getNom());

        // Refresh parent and return
        if (parentController != null) {
            // Trigger refresh of parent controller's task list
        }
        returnToPrevious();
    }

    private void returnToPrevious() {
        if (previousScene != null && retourBtn != null) {
            Stage stage = (Stage) retourBtn.getScene().getWindow();
            stage.setScene(previousScene);
        }
    }
}
