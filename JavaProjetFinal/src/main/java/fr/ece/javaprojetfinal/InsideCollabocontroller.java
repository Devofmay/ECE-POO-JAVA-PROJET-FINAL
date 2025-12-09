package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Utilisateur;
import fr.ece.javaprojetfinal.basics.UtilisateurDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class InsideCollabocontroller {

    @FXML
    private TableView<Utilisateur> tasksTable;

    @FXML
    private TableColumn<Utilisateur, String> taskNameColumn;

    @FXML
    private TableColumn<Utilisateur, String> creationDateColumn;

    @FXML
    private TableColumn<Utilisateur, String> dueDateColumn;

    @FXML
    private TableColumn<Utilisateur, String> ownerColumn;

    @FXML
    private TableColumn<Utilisateur, String> statusColumn;

    @FXML
    private TableColumn<Utilisateur, Utilisateur> editColumn;
    @FXML
    private Button ajoutertache;

    // wire the "Projets" button present in InsideCollabo.fxml
    @FXML
    private javafx.scene.control.Button projetsbtn;

    private final ObservableList<Utilisateur> users = FXCollections.observableArrayList();
    private final UtilisateurDAO dao = new UtilisateurDAO();

    @FXML
    private void initialize() {
        if (tasksTable != null) {
            // Bind columns to model getters (adapt names if your Utilisateur has different getter names)
            taskNameColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNom()));
            creationDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getAdresse()));
            dueDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getRole()));

            // Mask password in table for security
            ownerColumn.setCellValueFactory(cell -> {
                String pwd = cell.getValue().getMotDePasse();
                if (pwd == null || pwd.isEmpty()) return new ReadOnlyObjectWrapper<>("");
                String masked = "••••••";
                return new ReadOnlyObjectWrapper<>(masked);
            });

            // actions column: modifier / supprimer
            editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
            editColumn.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn = new Button("Modifier");
                private final Button delBtn = new Button("Supprimer");
                private final HBox container = new HBox(8, editBtn, delBtn);

                {
                    editBtn.setOnAction(e -> {
                        Utilisateur u = getItem();
                        if (u != null) openModifyDialog(u);
                    });
                    delBtn.setOnAction(e -> {
                        Utilisateur u = getItem();
                        if (u != null) deleteUser(u);
                    });
                    editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
                    delBtn.setStyle("-fx-background-color: #c74444; -fx-text-fill: white;");
                }

                @Override
                protected void updateItem(Utilisateur item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(container);
                    }
                }
            });

            tasksTable.setItems(users);
        }
        if (ajoutertache != null) {
            ajoutertache.setOnAction(e -> openAddUserDialog());
        }

        // wire the projets button to open the projects home (preserve styles if possible)
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
                    Alert err = new Alert(Alert.AlertType.ERROR, "Cannot open projects view: " + ex.getMessage(), ButtonType.OK);
                    err.showAndWait();
                }
            });
        }

        loadUsers();
    }

    private void loadUsers() {
        Platform.runLater(() -> {
            try {
                List<Utilisateur> list = dao.findAll();
                users.setAll(list);
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert err = new Alert(Alert.AlertType.ERROR, "Failed to load users: " + ex.getMessage(), ButtonType.OK);
                err.showAndWait();
            }
        });
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
                ex.printStackTrace();
                Alert err = new Alert(Alert.AlertType.ERROR, "Échec suppression : " + ex.getMessage(), ButtonType.OK);
                err.showAndWait();
            }
        }
    }

    private void openModifyDialog(Utilisateur u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/ModifierUser.fxml"));
            Parent root = loader.load();
            ModifierUsercontroller ctrl = loader.getController();
            ctrl.setUser(u);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Modifier utilisateur - " + u.getNom());
            Scene scene = new Scene(root);
            // preserve styles from current table scene if possible
            if (tasksTable != null && tasksTable.getScene() != null) {
                scene.getStylesheets().addAll(tasksTable.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.sizeToScene();
            dialog.showAndWait();

            // refresh table after dialog closes
            tasksTable.refresh();
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR, "Cannot open modifier window: " + ex.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }

    private void openAddUserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/AjouterUser.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Ajouter un collaborateur");
            Scene scene = new Scene(root);
            if (tasksTable != null && tasksTable.getScene() != null) {
                scene.getStylesheets().addAll(tasksTable.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.sizeToScene();
            dialog.showAndWait();

            // refresh table after dialog closes
            loadUsers();
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR, "Cannot open add user window: " + ex.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }
}
