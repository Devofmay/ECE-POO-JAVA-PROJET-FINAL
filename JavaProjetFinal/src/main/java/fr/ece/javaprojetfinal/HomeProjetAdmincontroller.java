package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.SettingsLauncher;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HomeProjetAdmincontroller {
    @FXML
    private BorderPane rootPane;
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
    @FXML
    private javafx.scene.control.Button utilisateursbtn;
    @FXML
    private javafx.scene.control.Button parametresbtn;
    @FXML
    private javafx.scene.control.Button calendrierbtn;

    private final ObservableList<Projet> projects = FXCollections.observableArrayList();
    private int loggedInUserId = -1;

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
                    } else if (rootPane != null) {
                        rootPane.setCenter(root);
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

    /**
     * Must be called after FXMLLoader.load() from the login flow.
     * Installs the Paramètres button handler with the correct user id.
     */
    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
        if (parametresbtn != null) {
            parametresbtn.setOnAction(ev -> {
                SettingsLauncher.openParametresForUser(this.loggedInUserId, (Node) parametresbtn);
            });
        }
    }

    // remaining methods unchanged (openProject, deleteProject)...
    public void openProject(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/InsideProjetAdmin.fxml"));
            Parent insideRoot = loader.load();

            InsideProjetAdminController insideController = loader.getController();
            insideController.setProject(projet);

            Stage currentStage = null;
            if (projectsTable != null && projectsTable.getScene() != null && projectsTable.getScene().getWindow() instanceof Stage) {
                currentStage = (Stage) projectsTable.getScene().getWindow();
            }

            if (currentStage != null) {
                Scene oldScene = projectsTable.getScene();
                Scene newScene = new Scene(insideRoot);
                if (oldScene != null) newScene.getStylesheets().addAll(oldScene.getStylesheets());
                currentStage.setScene(newScene);
                currentStage.setTitle("Projet - " + projet.getNom());
                currentStage.sizeToScene();
            } else if (rootPane != null) {
                rootPane.setCenter(insideRoot);
            } else {
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
