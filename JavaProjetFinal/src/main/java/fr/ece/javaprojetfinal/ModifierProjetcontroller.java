// java
package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModifierProjetcontroller {

    @FXML
    private TextField nomProjetField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker dateEcheancePicker;

    @FXML
    private ComboBox<String> responsableCombo;

    @FXML
    private ComboBox<String> statutCombo;

    @FXML
    private Button retourBtn;

    @FXML
    private Button annulerBtn;

    @FXML
    private Button enregistrerBtn;
    @FXML
    private javafx.scene.control.Button utilisateursbtn;
    @FXML
    private javafx.scene.control.Button projetsbtn;
    @FXML
    private javafx.scene.control.Button calendrierbtn;


    private Projet currentProjet;
    private Scene previousScene;
    private final Map<String, Integer> labelToUserId = new LinkedHashMap<>();

    // mapping DB status -> display label and the reverse
    private final Map<String, String> dbToLabel = new LinkedHashMap<>();
    private final Map<String, String> labelToDb = new LinkedHashMap<>();

    // reference to parent controller to request a refresh after save
    private InsideProjetAdminController parentController;

    @FXML
    private void initialize() {
        // configure status mappings: adjust DB keys if your DB uses different values
        // configure status mappings: map DB values -> display labels and reverse
        dbToLabel.clear();
// DB canonical values (from your SQL dump): "En cours", "Terminé"
        dbToLabel.put("En cours", "en cours");
        dbToLabel.put("Terminé", "Termine");

// build reverse map (display label -> DB value)
        labelToDb.clear();
        for (Map.Entry<String, String> e : dbToLabel.entrySet()) {
            labelToDb.put(e.getValue(), e.getKey());
        }

// populate statut combo with the display labels (order preserved)
        if (statutCombo != null) {
            statutCombo.getItems().setAll(labelToDb.keySet());
        }

// ensure SQLException shows full stacktrace in this controller when calling DAO
// (also update your ProjetDAO catch blocks to print stacktrace)
        try {
            ProjetDAO dao = new ProjetDAO();
            Map<Integer, String> users = dao.findAllUsers();
            labelToUserId.clear();
            for (Map.Entry<Integer, String> e : users.entrySet()) {
                String label = e.getValue() + " (" + e.getKey() + ")";
                labelToUserId.put(label, e.getKey());
            }
            if (responsableCombo != null) {
                responsableCombo.getItems().setAll(labelToUserId.keySet());
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // show full error and cause (helps identify constraint violations)
            System.err.println("Failed to load users: " + ex.getMessage());
        }

        // wire buttons if not using onAction in FXML
        if (retourBtn != null) retourBtn.setOnAction(this::onRetour);
        if (annulerBtn != null) annulerBtn.setOnAction(this::onAnnuler);
        if (enregistrerBtn != null) enregistrerBtn.setOnAction(this::onEnregistrer);
        if (utilisateursbtn != null) {
            utilisateursbtn.setOnAction(ev -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/InsideCollabo.fxml"));
                    Parent root = loader.load();

                    Stage stage = null;
                    Scene old = null;
                    if (utilisateursbtn.getScene() != null) {
                        old = utilisateursbtn.getScene();
                        if (old.getWindow() instanceof Stage) stage = (Stage) old.getWindow();
                    }

                    Scene newScene = new Scene(root);
                    if (old != null) newScene.getStylesheets().addAll(old.getStylesheets());

                    if (stage != null) {
                        stage.setScene(newScene);
                        stage.setTitle("Collaborateurs");
                        stage.sizeToScene();
                    } else {
                        Stage s = new Stage();
                        s.setScene(newScene);
                        s.setTitle("Collaborateurs");
                        s.show();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
        if (projetsbtn != null) {
            projetsbtn.setOnAction(ev -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeprojetsAdmin.fxml"));
                    Parent root = loader.load();
                    HomeProjetAdmincontroller adminController = loader.getController();
                    adminController.setLoggedInUserId(1);
                    adminController.setUser(1, "Admin", true);

                    Stage stage = null;
                    Scene old = null;
                    if (projetsbtn.getScene() != null) {
                        old = projetsbtn.getScene();
                        if (old.getWindow() instanceof Stage) stage = (Stage) old.getWindow();
                    }

                    Scene newScene = new Scene(root);
                    if (old != null) newScene.getStylesheets().addAll(old.getStylesheets());

                    if (stage != null) {
                        stage.setScene(newScene);
                        stage.setTitle("Mes projets");
                        stage.sizeToScene();
                    } else {
                        Stage s = new Stage();
                        s.setScene(newScene);
                        s.setTitle("Mes projets");
                        s.show();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
        if (calendrierbtn != null) {
            calendrierbtn.setOnAction(ev -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/Calendar.fxml"));
                    Parent root = loader.load();

                    Scene newScene = new Scene(root, 1000, 700);

                    // preserve stylesheets from the current scene if present
                    Scene oldScene = calendrierbtn.getScene();
                    if (oldScene != null) {
                        newScene.getStylesheets().addAll(oldScene.getStylesheets());
                    }

                    // Always open in a separate window (new Stage)
                    Stage calendarStage = new Stage();
                    calendarStage.setTitle("Mon Calendrier");
                    calendarStage.setScene(newScene);
                    calendarStage.sizeToScene();
                    calendarStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Alert err = new Alert(Alert.AlertType.ERROR, "Cannot open calendar: " + e.getMessage(), ButtonType.OK);
                    err.showAndWait();
                }
            });
        }

    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setParentController(InsideProjetAdminController parent) {
        this.parentController = parent;
    }

    public void setProject(Projet projet) {
        this.currentProjet = projet;
        if (projet == null) return;

        if (nomProjetField != null) nomProjetField.setText(projet.getNom());
        if (descriptionArea != null) descriptionArea.setText(projet.getDescription());

        if (dateEcheancePicker != null) {
            Date d = projet.getDateEcheance();
            if (d != null) {
                dateEcheancePicker.setValue(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else {
                dateEcheancePicker.setValue(null);
            }
        }

        // select responsable if present
        if (responsableCombo != null && projet.getResponsable() != null) {
            Integer respId = projet.getResponsable();
            String match = null;
            for (Map.Entry<String, Integer> entry : labelToUserId.entrySet()) {
                if (entry.getValue().equals(respId)) {
                    match = entry.getKey();
                    break;
                }
            }
            if (match != null) responsableCombo.getSelectionModel().select(match);
        }

        // select statut by mapping DB value to label
        if (statutCombo != null && projet.getStatut() != null) {
            String dbStatus = projet.getStatut();
            String label = dbToLabel.getOrDefault(dbStatus, dbStatus);
            if (!statutCombo.getItems().contains(label)) {
                // ensure unknown DB status is selectable
                statutCombo.getItems().add(label);
                labelToDb.put(label, dbStatus);
            }
            statutCombo.getSelectionModel().select(label);
        }
    }

    @FXML
    private void onAnnuler(ActionEvent event) {
        setProject(currentProjet);
    }

    @FXML
    private void onRetour(ActionEvent event) {
        Stage stage = getStageFromNode();
        if (stage == null) return;
        // ensure parent view is refreshed with current data
        if (parentController != null && currentProjet != null) {
            parentController.setProject(currentProjet);
        }
        if (previousScene != null) {
            stage.setScene(previousScene);
        } else {
            stage.close();
        }
    }

    @FXML
    private void onEnregistrer(ActionEvent event) {
        if (currentProjet == null) return;
        String nom = nomProjetField != null ? nomProjetField.getText() : null;
        String desc = descriptionArea != null ? descriptionArea.getText() : null;
        LocalDate ld = dateEcheancePicker != null ? dateEcheancePicker.getValue() : null;
        Date dateEcheance = ld != null ? Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

        Integer selectedResponsableId = null;
        if (responsableCombo != null) {
            String sel = responsableCombo.getSelectionModel().getSelectedItem();
            selectedResponsableId = sel != null ? labelToUserId.get(sel) : null;
        }

        // convert selected display label back to DB value
        String selectedLabel = statutCombo != null ? statutCombo.getSelectionModel().getSelectedItem() : null;
        String dbStatut = selectedLabel != null ? labelToDb.getOrDefault(selectedLabel, selectedLabel) : null;

        Projet updated = new Projet(
                currentProjet.getId(),
                nom,
                desc,
                currentProjet.getDateCreation(),
                dateEcheance,
                selectedResponsableId,
                dbStatut
        );

        try {
            ProjetDAO dao = new ProjetDAO();
            int updatedRows = dao.updateProjet(updated);
            if (updatedRows > 0) {
                // notify user
                LocalDateTime now = LocalDateTime.now();
                String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Modifications enregistrées le " + time + ".");
                alert.showAndWait();

                // refresh parent view if available
                if (parentController != null) {
                    parentController.setProject(updated);
                }

                // go back to previous scene or close
                Stage stage = getStageFromNode();
                if (stage != null && previousScene != null) {
                    stage.setScene(previousScene);
                } else if (stage != null) {
                    stage.close();
                }
            } else {
                System.err.println("No rows updated for project id " + currentProjet.getId());
                Alert err = new Alert(Alert.AlertType.WARNING, "Aucune modification enregistrée.", ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        } catch (SQLException ex) {
            System.err.println("Failed to update project: " + ex.getMessage());
            Alert err = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement: " + ex.getMessage(), ButtonType.OK);
            err.setHeaderText(null);
            err.showAndWait();
        }
    }

    private Stage getStageFromNode() {
        if (enregistrerBtn != null && enregistrerBtn.getScene() != null && enregistrerBtn.getScene().getWindow() instanceof Stage) {
            return (Stage) enregistrerBtn.getScene().getWindow();
        }
        if (retourBtn != null && retourBtn.getScene() != null && retourBtn.getScene().getWindow() instanceof Stage) {
            return (Stage) retourBtn.getScene().getWindow();
        }
        return null;
    }
}
