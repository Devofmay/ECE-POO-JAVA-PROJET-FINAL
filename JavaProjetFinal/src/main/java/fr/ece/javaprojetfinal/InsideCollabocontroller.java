package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Utilisateur;
import fr.ece.javaprojetfinal.basics.UtilisateurDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import java.util.List;
import java.util.Optional;

public class InsideCollabocontroller extends BaseController {

    @FXML private TableView<Utilisateur> tasksTable;
    @FXML private TableColumn<Utilisateur, String> taskNameColumn;
    @FXML private TableColumn<Utilisateur, String> creationDateColumn;
    @FXML private TableColumn<Utilisateur, String> dueDateColumn;
    @FXML private TableColumn<Utilisateur, String> ownerColumn;
    @FXML private TableColumn<Utilisateur, Utilisateur> editColumn;

    @FXML private Button ajoutertache;
    @FXML private Button calendrierbtn;
    @FXML private Button projetsbtn;
    @FXML private Button logoutBtn;

    private final ObservableList<Utilisateur> users = FXCollections.observableArrayList();
    private final UtilisateurDAO dao = new UtilisateurDAO();

    @FXML
    private void initialize() {
        initializeSession();

        taskNameColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getNom())
        );
        creationDateColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getAdresse())
        );
        dueDateColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getRole())
        );
        ownerColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        cell.getValue().getMotDePasse() != null ? "••••••" : ""
                )
        );

        editColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue())
        );
        editColumn.setCellFactory(col -> createActionsCell());

        tasksTable.setItems(users);

        ajoutertache.setOnAction(e -> openAddUserDialog());
        logoutBtn.setOnAction(this::handleLogout);
        projetsbtn.setOnAction(this::openProjectsPage);
        calendrierbtn.setOnAction(this::openCalendar);

        loadUsers();
    }

    @Override
    protected boolean checkPagePermissions() {
        return getSession().isAdmin();
    }

    private void loadUsers() {
        try {
            List<Utilisateur> list = dao.findAll();
            users.setAll(list);
        } catch (Exception ex) {
            showError("Impossible de charger les utilisateurs.");
        }
    }

    private TableCell<Utilisateur, Utilisateur> createActionsCell() {
        return new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button delBtn = new Button("Supprimer");
            private final HBox container = new HBox(8, editBtn, delBtn);

            {
                editBtn.setStyle("-fx-background-color:#3b82f6; -fx-text-fill:white;");
                delBtn.setStyle("-fx-background-color:#c74444; -fx-text-fill:white;");

                editBtn.setOnAction(e -> openModifyDialog(getItem()));
                delBtn.setOnAction(e -> deleteUser(getItem()));
            }

            @Override
            protected void updateItem(Utilisateur item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : container);
            }
        };
    }

    private void deleteUser(Utilisateur u) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer utilisateur");
        confirm.setHeaderText("Supprimer " + u.getNom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                dao.deleteById(u.getId());
                users.remove(u);
            } catch (Exception ex) {
                showError("Échec de la suppression.");
            }
        }
    }

    private void openModifyDialog(Utilisateur u) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fr/ece/javaprojetfinal/ModifierUser.fxml")
            );
            Parent root = loader.load();
            ModifierUsercontroller ctrl = loader.getController();
            ctrl.setUser(u);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Modifier utilisateur - " + u.getNom());
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            tasksTable.refresh();
        } catch (IOException ex) {
            showError("Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    private void openAddUserDialog() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fr/ece/javaprojetfinal/AjouterUser.fxml")
            );

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Ajouter un collaborateur");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            loadUsers();
        } catch (IOException ex) {
            showError("Impossible d'ouvrir la fenêtre d'ajout.");
        }
    }

    private void openCalendar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fr/ece/javaprojetfinal/Calendar.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Mon Calendrier");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            showError("Impossible d'ouvrir le calendrier.");
        }
    }

    private void openProjectsPage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fr/ece/javaprojetfinal/HomeprojetsAdmin.fxml")
            );

            Stage stage = (Stage) projetsbtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes projets - " + getCurrentUsername());
            stage.setMaximized(true);
        } catch (IOException ex) {
            showError("Impossible d'ouvrir la page projets.");
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
