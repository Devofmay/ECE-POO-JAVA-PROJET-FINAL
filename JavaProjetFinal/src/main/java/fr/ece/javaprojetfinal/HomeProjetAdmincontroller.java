package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HomeProjetAdmincontroller {
    @FXML
    private BorderPane rootPane; // add reference to top-level BorderPane

    @FXML
    private Label usernameSpot;

    @FXML
    private TableView<Projet> projectsTable;

    @FXML
    private TableColumn<Projet, String> nameCol;

    @FXML
    private TableColumn<Projet, String> descCol;

    @FXML
    private TableColumn<Projet, Projet> actionsCol;

    private final ObservableList<Projet> projects = FXCollections.observableArrayList();

    public void setUser(int userId, String username, boolean isAdmin) {
        if (usernameSpot != null) {
            usernameSpot.setText(username);
        }
        loadProjectsByRole(userId, isAdmin);
    }

    private void loadProjectsByRole(int userId, boolean isAdmin) {
        ProjetDAO dao = new ProjetDAO();
        try {
            List<Projet> projets;
            if (isAdmin) {
                projets = dao.findByResponsableId(userId);
            } else {
                projets = dao.findByUserId(userId);
            }
            final List<Projet> finalProjets = projets;
            Platform.runLater(() -> {
                projects.setAll(finalProjets);
                if (projectsTable != null) {
                    projectsTable.setItems(projects);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        if (projectsTable != null) {
            projectsTable.setPlaceholder(new Label("Aucun projet pour cet utilisateur"));

            nameCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNom()));
            descCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getDescription()));

            actionsCol.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
            actionsCol.setCellFactory(col -> new ActionsCell(this));

            projectsTable.setItems(projects);
        }
    }


    public void openProject(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/InsideProjetAdmin.fxml"));
            Parent insideRoot = loader.load();

            InsideProjetAdminController insideController = loader.getController();
            insideController.setProject(projet);

            // try to get the current Stage from a UI node (projectsTable)
            Stage currentStage = null;
            if (projectsTable != null && projectsTable.getScene() != null && projectsTable.getScene().getWindow() instanceof Stage) {
                currentStage = (Stage) projectsTable.getScene().getWindow();
            }

            if (currentStage != null) {
                Scene oldScene = projectsTable.getScene();
                Scene newScene = new Scene(insideRoot);
                // preserve stylesheets from the old scene if present
                if (oldScene != null) {
                    newScene.getStylesheets().addAll(oldScene.getStylesheets());
                }
                currentStage.setScene(newScene);
                currentStage.setTitle("Projet - " + projet.getNom());
                currentStage.sizeToScene();
            } else if (rootPane != null) {
                // fallback: replace center of current BorderPane
                rootPane.setCenter(insideRoot);
            } else {
                // last-resort: open a new window (shouldn't happen for usual usage)
                Stage stage = new Stage();
                stage.setScene(new Scene(insideRoot));
                stage.setTitle("Projet - " + projet.getNom());
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteProject(Projet projet) {
        ProjetDAO dao = new ProjetDAO();
        try {
            dao.deleteById(projet.getId());
            Platform.runLater(() -> projects.remove(projet));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodError ex) {
            projects.remove(projet);
        }
    }
}
