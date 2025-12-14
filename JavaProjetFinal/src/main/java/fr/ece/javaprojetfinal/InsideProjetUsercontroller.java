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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InsideProjetUsercontroller extends BaseController {

    @FXML private TextField projectNameField;
    @FXML private ListView<String> collaboratorsList;

    @FXML private TableView<Tache> tasksTable;
    @FXML private TableColumn<Tache, String> taskNameColumn;
    @FXML private TableColumn<Tache, String> creationDateColumn;
    @FXML private TableColumn<Tache, String> dueDateColumn;
    @FXML private TableColumn<Tache, String> ownerColumn;
    @FXML private TableColumn<Tache, String> statusColumn;
    @FXML private TableColumn<Tache, Tache> actionsColumn;

    @FXML private Button logoutBtn;
    @FXML private Button parametresBtn;
    @FXML private Button calendrierbtn;
    @FXML private Button projetsbtn;
    @FXML private Button tachesbtn;

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
                new ReadOnlyStringWrapper(safeGetString(c.getValue(), new String[]{"getOwnerName", "getResponsable", "getResponsableName", "getAssignee"}))
        );


        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getStatut())
        );

        actionsColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue())
        );

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("Voir");

            {
                viewBtn.setStyle("-fx-background-radius:6; -fx-background-color:#3b82f6; -fx-text-fill:white;");
                viewBtn.setOnAction(e -> viewTask(getItem()));
            }

            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : viewBtn);
            }
        });

        tasksTable.setItems(tasks);

        logoutBtn.setOnAction(this::handleLogout);

        parametresBtn.setOnAction(e ->
                SettingsLauncher.openParametresForUser(getCurrentUserId(), parametresBtn)
        );

        calendrierbtn.setOnAction(this::openCalendar);
        projetsbtn.setOnAction(this::openProjectsPage);
        tachesbtn.setOnAction(this::openTasksPage);
    }

    @Override
    protected boolean checkPagePermissions() {
        return !getSession().isAdmin();
    }

    public void setProject(Projet projet) {
        this.currentProjet = projet;
        projectNameField.setText(projet.getNom());

        try {
            ProjetDAO dao = new ProjetDAO();

            List<String> collaborators =
                    dao.findCollaboratorsNamesByProjetId(projet.getId());
            List<Tache> projectTasks =
                    dao.findTasksByProjetId(projet.getId());

            collaboratorsList.getItems().setAll(collaborators);
            tasks.setAll(projectTasks);

        } catch (SQLException e) {
            showErrorAndExit("Erreur lors du chargement du projet.");
        }
    }

    private void viewTask(Tache t) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewTacheUser.fxml"));
            Parent root = loader.load();

            ViewTacheUsercontroller ctrl = loader.getController();
            ctrl.setTask(t);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Tâche - " + t.getNom());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showErrorAndExit("Impossible d’ouvrir la tâche.");
        }
    }

    private void openCalendar(ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/Calendar.fxml", "Mon Calendrier");
    }

    private void openProjectsPage(ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/HomeUserProjets.fxml",
                "Mes projets - " + getCurrentUsername());
    }

    private void openTasksPage(ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/HomeUsertaches.fxml",
                "Mes tâches - " + getCurrentUsername());
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
    private String safeGetString(Tache t, String[] getterNames) {
        if (t == null) return "";
        for (String g : getterNames) {
            try {
                java.lang.reflect.Method m = t.getClass().getMethod(g);
                Object val = m.invoke(t);
                if (val != null) return String.valueOf(val);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ex) {
                System.err.println("Reflection error calling " + g + ": " + ex.getMessage());
            }
        }
        return "";
    }

}
