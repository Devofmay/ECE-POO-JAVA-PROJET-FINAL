package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.Tache;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InsideProjetAdminController {

    @FXML
    private TextField projectNameField;

    @FXML
    private Label usernameField;

    @FXML
    private ListView<String> collaboratorsList;

    @FXML
    private TableView<Tache> tasksTable;

    @FXML
    private TableColumn<Tache, String> taskNameColumn;

    @FXML
    private TableColumn<Tache, String> creationDateColumn;

    @FXML
    private TableColumn<Tache, String> dueDateColumn;

    @FXML
    private TableColumn<Tache, String> ownerColumn;

    @FXML
    private TableColumn<Tache, String> statusColumn;

    // Button in the FXML
    @FXML
    private Button modifprojet;

    private final ObservableList<Tache> taskNames = FXCollections.observableArrayList();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // store the current project so modifier can receive it
    private Projet currentProjet;

    @FXML
    private void initialize() {
        if (tasksTable != null) {
            taskNameColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNom()));
            creationDateColumn.setCellValueFactory(cell -> {
                LocalDate d = cell.getValue().getDateCreation();
                return new ReadOnlyObjectWrapper<>(d != null ? dateFormat.format(d) : "");
            });
            dueDateColumn.setCellValueFactory(cell -> {
                java.util.Date d = cell.getValue().getDateEcheances();
                return new ReadOnlyObjectWrapper<>(d != null ? dateFormat.format(d) : "");
            });
            ownerColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getOwnerName()));
            statusColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStatut()));

            tasksTable.setItems(taskNames);
        }

        if (collaboratorsList != null) {
            collaboratorsList.getItems().clear();
        }

        // wire button action (optional if declared in FXML)
        if (modifprojet != null) {
            modifprojet.setOnAction(e -> openModifierProjet());
        }
    }

    public void setProject(Projet projet) {
        if (projet == null) return;
        this.currentProjet = projet;

        if (projectNameField != null) {
            projectNameField.setText(projet.getNom());
        }

        Platform.runLater(() -> {
            List<String> collaborators = new ArrayList<>();
            List<Tache> tasks = new ArrayList<>();
            String responsableName = null;
            try {
                ProjetDAO dao = new ProjetDAO();
                collaborators = dao.findCollaboratorsNamesByProjetId(projet.getId());
                tasks = dao.findTasksByProjetId(projet.getId());
                responsableName = dao.getResponsableNameByProjetId(projet.getId());
            } catch (SQLException e) {
                System.err.println("DB error loading project details: " + e.getMessage());
            }

            if (responsableName == null) responsableName = "";
            for (Tache t : tasks) {
                t.setOwnerName(responsableName);
            }

            if (collaboratorsList != null) {
                collaboratorsList.getItems().setAll(collaborators);
            }
            taskNames.setAll(tasks);
            if (usernameField != null) {
                usernameField.setText(responsableName);
            }
        });
    }

    // Open the modifier page in the same window (replace Scene)
    private void openModifierProjet() {
        if (currentProjet == null) return;
        try {
            // find the current stage and current scene to restore later
            Stage stage = null;
            Scene oldScene = null;
            if (projectNameField != null && projectNameField.getScene() != null && projectNameField.getScene().getWindow() instanceof Stage) {
                stage = (Stage) projectNameField.getScene().getWindow();
                oldScene = projectNameField.getScene();
            } else if (tasksTable != null && tasksTable.getScene() != null && tasksTable.getScene().getWindow() instanceof Stage) {
                stage = (Stage) tasksTable.getScene().getWindow();
                oldScene = tasksTable.getScene();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/ModifierProjet.fxml"));
            Parent modifierRoot = loader.load();

            // pass references to the modifier controller so it can refresh this view
            Object controllerObj = loader.getController();
            if (controllerObj instanceof fr.ece.javaprojetfinal.ModifierProjetcontroller) {
                fr.ece.javaprojetfinal.ModifierProjetcontroller controller = (fr.ece.javaprojetfinal.ModifierProjetcontroller) controllerObj;
                controller.setProject(currentProjet);
                controller.setPreviousScene(oldScene);
                controller.setParentController(this); // allow callback to refresh
            } else {
                if (controllerObj != null) {
                    try {
                        controllerObj.getClass().getMethod("setProject", Projet.class).invoke(controllerObj, currentProjet);
                        controllerObj.getClass().getMethod("setPreviousScene", Scene.class).invoke(controllerObj, oldScene);
                        controllerObj.getClass().getMethod("setParentController", InsideProjetAdminController.class).invoke(controllerObj, this);
                    } catch (NoSuchMethodException ignored) {
                        // controller doesn't expose parent setter; fallback behavior remains
                    } catch (Exception ex) {
                        System.err.println("Failed to set modifier controller references: " + ex.getMessage());
                    }
                }
            }

            if (stage != null) {
                Scene newScene = new Scene(modifierRoot);
                if (oldScene != null) newScene.getStylesheets().addAll(oldScene.getStylesheets());
                stage.setScene(newScene);
                stage.setTitle("Modifier le projet - " + currentProjet.getNom());
                stage.sizeToScene();
            } else {
                Stage s = new Stage();
                s.setScene(new Scene(modifierRoot));
                s.setTitle("Modifier le projet - " + currentProjet.getNom());
                s.show();
            }

        } catch (IOException ex) {
            System.err.println("Failed to load ModifierProjet.fxml: " + ex.getMessage());
        }
    }
}
