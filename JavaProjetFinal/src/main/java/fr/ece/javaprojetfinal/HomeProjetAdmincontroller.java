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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HomeProjetAdmincontroller extends BaseController {

    @FXML private BorderPane rootPane;
    @FXML private Label usernameSpot;
    @FXML private TableView<Projet> projectsTable;
    @FXML private TableColumn<Projet, String> nameCol;
    @FXML private TableColumn<Projet, String> descCol;
    @FXML private TableColumn<Projet, Projet> actionsCol;
    @FXML private Button utilisateursbtn;
    @FXML private Button parametresbtn;
    @FXML private Button calendrierbtn;
    @FXML private Button creerProjetBtn;

    private final ObservableList<Projet> projects = FXCollections.observableArrayList();

    //  Permissions : ADMIN ONLY
    @Override
    protected boolean checkPagePermissions() {
        return getSession().isAdmin();
    }

    @FXML
    void initialize() {

        // 🔐 Initialisation de session (OBLIGATOIRE)
        initializeSession();

        // Table configuration
        projectsTable.setPlaceholder(new Label("Aucun projet pour cet administrateur"));
        nameCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNom()));
        descCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getDescription()));
        actionsCol.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
        actionsCol.setCellFactory(col -> new ActionsCell(this));
        projectsTable.setItems(projects);

        //  Charger les projets de l'admin connecté
        loadProjectsForAdmin();

        //  Navigation Collaborateurs
        utilisateursbtn.setOnAction(ev -> navigate("/fr/ece/javaprojetfinal/InsideCollabo.fxml", "Collaborateurs"));

        //  Calendrier (nouvelle fenêtre)
        calendrierbtn.setOnAction(ev -> openCalendar());

        // ⚙ Paramètres (basé sur la session)
        parametresbtn.setOnAction(ev ->
                SettingsLauncher.openParametresForUser(getCurrentUserId(), (Node) parametresbtn)
        );

        if (creerProjetBtn != null) {
            creerProjetBtn.setOnAction(ev -> openAjouterProjet());
        }
    }

    // ===================== LOGIQUE MÉTIER =====================

    private void loadProjectsForAdmin() {
        ProjetDAO dao = new ProjetDAO();
        try {
            List<Projet> projets = dao.findByResponsableId(getCurrentUserId());
            Platform.runLater(() -> projects.setAll(projets));
        } catch (SQLException e) {
            showErrorAndExit("Erreur lors du chargement des projets.");
        }
    }

    public void openProject(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/InsideProjetAdmin.fxml"));
            Parent root = loader.load();

            InsideProjetAdminController controller = loader.getController();
            controller.setProject(projet);

            switchScene(root, "Projet - " + projet.getNom());

        } catch (IOException e) {
            showErrorAndExit("Impossible d’ouvrir le projet.");
        }
    }

    public void deleteProject(Projet projet) {
        try {
            new ProjetDAO().deleteById(projet.getId());
            projects.remove(projet);
        } catch (SQLException e) {
            showError("Erreur lors de la suppression du projet.");
        }
    }

    // ===================== UTILITAIRES NAVIGATION =====================

    private void navigate(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            switchScene(root, title);
        } catch (IOException e) {
            showError("Navigation impossible.");
        }
    }

    private void switchScene(Parent root, String title) {
        Stage stage = (Stage) projectsTable.getScene().getWindow();
        Scene oldScene = stage.getScene();
        Scene newScene = new Scene(root);
        if (oldScene != null) newScene.getStylesheets().addAll(oldScene.getStylesheets());
        stage.setScene(newScene);
        stage.setTitle(title);
        stage.setMaximized(true);
    }

    private void openCalendar() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fr/ece/javaprojetfinal/Calendar.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Calendrier");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            showError("Impossible d’ouvrir le calendrier.");
        }
    }

    private void openAjouterProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/AjouterProjet.fxml"));
            Parent root = loader.load();

            AjouterProjetcontroller ctrl = loader.getController();
            ctrl.setParentController(this);
            ctrl.setPreviousScene(creerProjetBtn.getScene());

            Stage dialog = new Stage();
            dialog.initOwner(creerProjetBtn.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Créer un projet");
            dialog.setScene(new Scene(root));
            dialog.sizeToScene();
            dialog.showAndWait();

            // recharger les projets après création
            loadProjectsForAdmin();
        } catch (IOException e) {
            showError("Impossible d’ouvrir la fenêtre de création de projet.");
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
