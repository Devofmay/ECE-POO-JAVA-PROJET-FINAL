package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.SettingsLauncher;
import fr.ece.javaprojetfinal.basics.Tache;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class InsideProjetAdminController extends BaseController {

    @FXML private TextField projectNameField;
    @FXML private ListView<String> collaboratorsList;

    @FXML private TableView<Tache> tasksTable;
    @FXML private TableColumn<Tache, String> taskNameColumn;
    @FXML private TableColumn<Tache, String> creationDateColumn;
    @FXML private TableColumn<Tache, String> dueDateColumn;
    @FXML private TableColumn<Tache, String> ownerColumn;
    @FXML private TableColumn<Tache, String> statusColumn;
    @FXML private TableColumn<Tache, Tache> actionsColumn;

    @FXML private Button modifprojet;
    @FXML private Button supprprojet;
    @FXML private Button ajoutertache;
    @FXML private Button logoutBtn;
    @FXML private Button parametresBtn;
    @FXML private Button calendrierbtn;
    @FXML private Button projetsbtn;
    @FXML private Button collabobtn;

    private final ObservableList<Tache> tasks = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Projet currentProjet;

    @FXML
    private void initialize() {
        initializeSession();

        taskNameColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getNom())
        );

        creationDateColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getDateCreation() != null
                                ? c.getValue().getDateCreation().format(dateFormatter)
                                : ""
                )
        );

        dueDateColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getDateEcheance() != null
                                ? c.getValue().getDateEcheance().format(dateFormatter)
                                : ""
                )
        );

        ownerColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getResponsable())
        );

        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getStatut())
        );

        actionsColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue())
        );

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox container = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color:#3b82f6; -fx-text-fill:white;");
                deleteBtn.setStyle("-fx-background-color:#c74444; -fx-text-fill:white;");

                editBtn.setOnAction(e -> openEditTaskDialog(getItem()));
                deleteBtn.setOnAction(e -> deleteTask(getItem()));
            }

            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : container);
            }
        });

        tasksTable.setItems(tasks);

        logoutBtn.setOnAction(this::handleLogout);

        parametresBtn.setOnAction(e ->
                SettingsLauncher.openParametresForUser(getCurrentUserId(), parametresBtn)
        );

        modifprojet.setOnAction(e -> openEditProjectDialog());
        supprprojet.setOnAction(e -> deleteProject());
        ajoutertache.setOnAction(e -> openAddTaskDialog());

        calendrierbtn.setOnAction(this::openCalendar);
        projetsbtn.setOnAction(this::openProjectsPage);
        collabobtn.setOnAction(this::openCollaboratorsPage);
    }

    @Override
    protected boolean checkPagePermissions() {
        return getSession().isAdmin();
    }

    public void setProject(Projet projet) {
        this.currentProjet = projet;
        projectNameField.setText(projet.getNom());

        try {
            ProjetDAO dao = new ProjetDAO();
            List<String> collaborators = dao.findCollaboratorsNamesByProjetId(projet.getId());
            List<Tache> projectTasks = dao.findTasksByProjetId(projet.getId());

            collaboratorsList.getItems().setAll(collaborators);
            tasks.setAll(projectTasks);

        } catch (SQLException e) {
            showErrorAndExit("Erreur lors du chargement du projet.");
        }
    }

    private void openEditTaskDialog(Tache t) {
        if (t == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifierTache.fxml"));
            Parent root = loader.load();
            ModifierTachecontroller ctrl = loader.getController();
            ctrl.setTask(t);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            tasksTable.refresh();
        } catch (IOException e) {
            showErrorAndExit("Impossible d’ouvrir la modification.");
        }
    }

    private void deleteTask(Tache t) {
        if (t == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Supprimer " + t.getNom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                new ProjetDAO().deleteTaskById(t.getId());
                tasks.remove(t);
            } catch (Exception e) {
                showErrorAndExit("Suppression impossible.");
            }
        }
    }

    private void openEditProjectDialog() {
        if (currentProjet == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifierProjet.fxml"));
            Parent root = loader.load();
            ModifierProjetcontroller ctrl = loader.getController();
            ctrl.setProject(currentProjet);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            projectNameField.setText(currentProjet.getNom());
        } catch (IOException e) {
            showErrorAndExit("Modification impossible.");
        }
    }

    private void deleteProject() {
        if (currentProjet == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Supprimer " + currentProjet.getNom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                new ProjetDAO().deleteById(currentProjet.getId());
                openProjectsPage(null);
            } catch (Exception e) {
                showErrorAndExit("Suppression impossible.");
            }
        }
    }

    private void openAddTaskDialog() {
        if (currentProjet == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AjouterTache.fxml"));
            Parent root = loader.load();
            AjouterTachecontroller ctrl = loader.getController();
            ctrl.setProject(currentProjet);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            setProject(currentProjet);
        } catch (IOException e) {
            showErrorAndExit("Ajout impossible.");
        }
    }

    private void openCalendar(ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/Calendar.fxml", "Mon Calendrier");
    }

    private void openProjectsPage(ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/HomeprojetsAdmin.fxml",
                "Mes projets - " + getCurrentUsername());
    }

    private void openCollaboratorsPage(ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/InsideCollabo.fxml", "Collaborateurs");
    }

    private void openSimplePage(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) tasksTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setMaximized(true);
        } catch (IOException ex) {
            showErrorAndExit("Navigation impossible.");
        }
    }


    public Projet getCurrentProjet() {
        return currentProjet;
    }

}
