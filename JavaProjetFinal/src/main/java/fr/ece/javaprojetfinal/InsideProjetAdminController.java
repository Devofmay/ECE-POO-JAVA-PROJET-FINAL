package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.Tache;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

    private final ObservableList<Tache> taskNames = FXCollections.observableArrayList();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    private void initialize() {
        if (tasksTable != null) {
            taskNameColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNom()));
            creationDateColumn.setCellValueFactory(cell -> {
                java.util.Date d = cell.getValue().getDateCreation();
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
    }

    // Call from opener to populate UI (runs in same window because opener sets center)
    public void setProject(Projet projet) {
        if (projet == null) return;

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

            // set owner name on each task (project responsable)
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
}
