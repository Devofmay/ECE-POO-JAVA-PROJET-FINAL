package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ModifierProjetcontroller extends BaseController {

    @FXML private TextField nomProjetField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateEcheancePicker;
    @FXML private ComboBox<String> responsableCombo;
    @FXML private ComboBox<String> statutCombo;
    @FXML private Button retourBtn;
    @FXML private Button annulerBtn;
    @FXML private Button enregistrerBtn;

    private Projet currentProjet;
    private Scene previousScene;
    private InsideProjetAdminController parentController;

    private final Map<String, Integer> labelToUserId = new LinkedHashMap<>();
    private final Map<String, String> dbToLabel = new LinkedHashMap<>();
    private final Map<String, String> labelToDb = new LinkedHashMap<>();

    // =========================
    // INITIALISATION
    // =========================
    @FXML
    private void initialize() {
        initializeSession();

        dbToLabel.put("En cours", "En cours");
        dbToLabel.put("Terminé", "Terminé");

        labelToDb.clear();
        for (Map.Entry<String, String> e : dbToLabel.entrySet()) {
            labelToDb.put(e.getValue(), e.getKey());
        }

        statutCombo.getItems().setAll(labelToDb.keySet());

        try {
            ProjetDAO dao = new ProjetDAO();
            Map<Integer, String> users = dao.findAllUsers();

            labelToUserId.clear();
            for (Map.Entry<Integer, String> e : users.entrySet()) {
                labelToUserId.put(e.getValue(), e.getKey());
            }
            responsableCombo.getItems().setAll(labelToUserId.keySet());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Impossible de charger les responsables");
        }

        retourBtn.setOnAction(this::onRetour);
        annulerBtn.setOnAction(this::onAnnuler);
        enregistrerBtn.setOnAction(this::onEnregistrer);
    }

    // =========================
    // SÉCURITÉ
    // =========================
    @Override
    protected boolean checkPagePermissions() {
        return getSession().isAdmin();
    }

    // =========================
    // SETTERS
    // =========================
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setParentController(InsideProjetAdminController parent) {
        this.parentController = parent;
    }

    public void setProject(Projet projet) {
        this.currentProjet = projet;
        if (projet == null) return;

        nomProjetField.setText(projet.getNom());
        descriptionArea.setText(projet.getDescription());

        if (projet.getDateEcheance() != null) {
            dateEcheancePicker.setValue(
                    projet.getDateEcheance().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
            );
        }

        if (projet.getResponsable() != null) {
            labelToUserId.forEach((label, id) -> {
                if (id.equals(projet.getResponsable())) {
                    responsableCombo.getSelectionModel().select(label);
                }
            });
        }

        if (projet.getStatut() != null) {
            statutCombo.getSelectionModel()
                    .select(dbToLabel.getOrDefault(projet.getStatut(), projet.getStatut()));
        }
    }

    // =========================
    // ACTIONS
    // =========================
    private void onAnnuler(ActionEvent e) {
        setProject(currentProjet);
    }

    private void onRetour(ActionEvent e) {
        Stage stage = getStage();
        if (parentController != null && currentProjet != null) {
            parentController.setProject(currentProjet);
        }
        if (previousScene != null) {
            stage.setScene(previousScene);
        } else {
            stage.close();
        }
    }

    private void onEnregistrer(ActionEvent e) {
        if (currentProjet == null) return;

        LocalDate ld = dateEcheancePicker.getValue();
        Date dateEcheance = ld != null
                ? Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        String statutLabel = statutCombo.getValue();
        String statutDb = labelToDb.getOrDefault(statutLabel, statutLabel);

        Integer responsableId = labelToUserId.get(responsableCombo.getValue());

        Projet updated = new Projet(
                currentProjet.getId(),
                nomProjetField.getText(),
                descriptionArea.getText(),
                currentProjet.getDateCreation(),
                dateEcheance,
                responsableId,
                statutDb
        );

        try {
            ProjetDAO dao = new ProjetDAO();
            dao.updateProjet(updated);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("Projet mis à jour le " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ok.showAndWait();

            if (parentController != null) {
                parentController.setProject(updated);
            }

            onRetour(null);

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erreur lors de l’enregistrement");
        }
    }

    private Stage getStage() {
        return (Stage) enregistrerBtn.getScene().getWindow();
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
