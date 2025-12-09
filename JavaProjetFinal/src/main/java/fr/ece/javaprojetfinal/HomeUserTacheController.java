package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Tache;
import fr.ece.javaprojetfinal.basics.TacheDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeUserTacheController {
    @FXML
    private Label usernameSpot;

    // Table view & columns kept for compatibility but UI no longer includes the TableView.
    @FXML
    private TableView<Tache> tasksTable;

    @FXML
    private TableColumn<Tache, String> nameCol;

    @FXML
    private TableColumn<Tache, String> projectCol;

    @FXML
    private TableColumn<Tache, String> dateCol;

    @FXML
    private TableColumn<Tache, Tache> actionsCol;

    // card labels (from FXML)
    @FXML private Label projetnamespot1;
    @FXML private Label taskspot1;
    @FXML private Label datespot1;
    @FXML private Label projetnamespot2;
    @FXML private Label taskspot2;
    @FXML private Label datespot2;
    @FXML private Label projetnamespot3;
    @FXML private Label taskspot3;
    @FXML private Label datespot3;
    @FXML private Label projetnamespot4;
    @FXML private Label taskspot4;
    @FXML private Label datespot4;

    private final ObservableList<Tache> tasks = FXCollections.observableArrayList();
    private int userId;

    // arrays to simplify mapping
    private List<Label> projetLabels;
    private List<Label> taskLabels;
    private List<Label> dateLabels;

    public void setUser(int userId, String username) {
        this.userId = userId;
        if (usernameSpot != null) usernameSpot.setText(username);
        loadTasksForUser();
    }

    @FXML
    private void initialize() {
        // build label lists for the 4 cards
        projetLabels = new ArrayList<>();
        projetLabels.add(projetnamespot1);
        projetLabels.add(projetnamespot2);
        projetLabels.add(projetnamespot3);
        projetLabels.add(projetnamespot4);

        taskLabels = new ArrayList<>();
        taskLabels.add(taskspot1);
        taskLabels.add(taskspot2);
        taskLabels.add(taskspot3);
        taskLabels.add(taskspot4);

        dateLabels = new ArrayList<>();
        dateLabels.add(datespot1);
        dateLabels.add(datespot2);
        dateLabels.add(datespot3);
        dateLabels.add(datespot4);

        // If a TableView is present in the scene it will be configured, otherwise skip.
        if (tasksTable != null) {
            nameCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNom()));
            projectCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getProjetNom()));
            dateCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
                    cell.getValue().getDateEcheance() != null ? cell.getValue().getDateEcheance().toString() : ""
            ));

            // actions column: show button to access task
            actionsCol.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
            actionsCol.setCellFactory(col -> new TaskActionsCell(this));

            tasksTable.setItems(tasks);
            tasksTable.setPlaceholder(new Label("Aucune tâche pour cet utilisateur"));
        }
    }

    private void loadTasksForUser() {
        Platform.runLater(() -> {
            TacheDAO dao = new TacheDAO();
            try {
                List<Tache> result = dao.findByUserId(userId); // DAO already orders by Date_echeances ASC
                tasks.setAll(result);
                updateCards(result);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // update the four preview cards under the table
    private void updateCards(List<Tache> result) {
        // ensure run on FX thread
        Platform.runLater(() -> {
            for (int i = 0; i < 4; i++) {
                if (i < result.size()) {
                    Tache t = result.get(i);
                    setCard(i, t);
                } else {
                    clearCard(i);
                }
            }
        });
    }

    private void setCard(int index, Tache t) {
        Label projetLabel = projetLabels.get(index);
        Label taskLabel = taskLabels.get(index);
        Label dateLabel = dateLabels.get(index);

        projetLabel.setText(t.getProjetNom() != null ? t.getProjetNom() : "-");
        taskLabel.setText(t.getNom() != null ? t.getNom() : "-");
        dateLabel.setText(t.getDateEcheance() != null ? t.getDateEcheance().toString() : "-");

        // ensure we don't add duplicate buttons: remove existing Button children in the parent VBox
        Node parent = projetLabel.getParent(); // both labels are in same VBox
        if (parent instanceof VBox) {
            VBox box = (VBox) parent;
            // remove existing Buttons (previous loads)
            box.getChildren().removeIf(node -> node instanceof Button);

            // create access button and add it
            Button access = new Button("Accéder");
            access.setStyle("-fx-background-radius:6; -fx-background-color: #3b82f6; -fx-text-fill: white;");
            access.setOnAction(e -> openTask(t));
            box.getChildren().add(access);
        }
    }

    private void clearCard(int index) {
        Label projetLabel = projetLabels.get(index);
        Label taskLabel = taskLabels.get(index);
        Label dateLabel = dateLabels.get(index);

        projetLabel.setText("-");
        taskLabel.setText("-");
        dateLabel.setText("-");

        Node parent = projetLabel.getParent();
        if (parent instanceof VBox) {
            VBox box = (VBox) parent;
            box.getChildren().removeIf(node -> node instanceof Button);
        }
    }

    // simple task opener: opens a small window showing basic task info
    public void openTask(Tache t) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.NONE);
            stage.setTitle("Tâche - " + t.getNom());

            Label name = new Label("Tâche: " + t.getNom());
            Label projet = new Label("Projet: " + (t.getProjetNom() != null ? t.getProjetNom() : "-"));
            Label due = new Label("Date d'échéance: " + (t.getDateEcheance() != null ? t.getDateEcheance().toString() : "-"));
            VBox box = new VBox(8, name, projet, due);
            box.setStyle("-fx-padding:12; -fx-background-color:white;");

            stage.setScene(new Scene(box));
            stage.sizeToScene();
            stage.show();
        });
    }
}
