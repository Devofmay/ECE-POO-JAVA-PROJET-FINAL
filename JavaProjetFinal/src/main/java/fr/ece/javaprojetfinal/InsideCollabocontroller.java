package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Utilisateur;
import fr.ece.javaprojetfinal.basics.UtilisateurDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Modifier utilisateur - " + u.getNom());

        TextField nameField = new TextField(u.getNom());
        TextField addrField = new TextField(u.getAdresse());
        TextField roleField = new TextField(u.getRole());
        PasswordField pwdField = new PasswordField();
        pwdField.setText(u.getMotDePasse());

        Button save = new Button("Enregistrer");
        Button cancel = new Button("Annuler");

        save.setOnAction(ev -> {
            u.setNom(nameField.getText());
            u.setAdresse(addrField.getText());
            u.setRole(roleField.getText());
            u.setMotDePasse(pwdField.getText());
            try {
                dao.update(u);
                tasksTable.refresh();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert err = new Alert(Alert.AlertType.ERROR, "Update failed: " + ex.getMessage(), ButtonType.OK);
                err.showAndWait();
            }
            dialog.close();
        });

        cancel.setOnAction(ev -> dialog.close());

        VBox content = new VBox(8,
                new Label("Nom"), nameField,
                new Label("Adresse"), addrField,
                new Label("Rôle (Admin/User)"), roleField,
                new Label("Mot de passe"), pwdField,
                new HBox(8, save, cancel)
        );
        content.setStyle("-fx-padding:12; -fx-background-color:white;");
        dialog.setScene(new javafx.scene.Scene(content));
        dialog.sizeToScene();
        dialog.showAndWait();
    }
}
