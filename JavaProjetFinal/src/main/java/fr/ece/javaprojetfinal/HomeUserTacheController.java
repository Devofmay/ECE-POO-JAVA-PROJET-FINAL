package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.SettingsLauncher;
import fr.ece.javaprojetfinal.basics.Tache;
import fr.ece.javaprojetfinal.basics.TacheDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeUserTacheController extends BaseController {

    @FXML private TableView<Tache> tasksTable;
    @FXML private TableColumn<Tache, String> nameCol;
    @FXML private TableColumn<Tache, String> projectCol;
    @FXML private TableColumn<Tache, String> dateCol;
    @FXML private TableColumn<Tache, Tache> actionsCol;

    @FXML private Button parametresBtn;
    @FXML private Button calendrierbtn;
    @FXML private Button tachesbtn;
    @FXML private Button logoutbtn;

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

    private List<Label> projetLabels;
    private List<Label> taskLabels;
    private List<Label> dateLabels;

    @FXML
    private void initialize() {
        initializeSession();

        projetLabels = List.of(
                projetnamespot1, projetnamespot2, projetnamespot3, projetnamespot4
        );

        taskLabels = List.of(
                taskspot1, taskspot2, taskspot3, taskspot4
        );

        dateLabels = List.of(
                datespot1, datespot2, datespot3, datespot4
        );

        if (tasksTable != null) {
            nameCol.setCellValueFactory(cell ->
                    new ReadOnlyObjectWrapper<>(cell.getValue().getNom())
            );
            projectCol.setCellValueFactory(cell ->
                    new ReadOnlyObjectWrapper<>(cell.getValue().getProjetNom())
            );
            dateCol.setCellValueFactory(cell ->
                    new ReadOnlyObjectWrapper<>(
                            cell.getValue().getDateEcheance() != null
                                    ? cell.getValue().getDateEcheance().toString()
                                    : ""
                    )
            );

            actionsCol.setCellValueFactory(col ->
                    new ReadOnlyObjectWrapper<>(col.getValue())
            );
            actionsCol.setCellFactory(col ->
                    new TaskActionsCell(this)
            );

            tasksTable.setItems(tasks);
            tasksTable.setPlaceholder(new Label("Aucune tâche pour cet utilisateur"));
        }

        if (logoutbtn != null) {
            logoutbtn.setOnAction(this::handleLogout);
        }

        if (parametresBtn != null) {
            parametresBtn.setOnAction(evt ->
                    SettingsLauncher.openParametresForUser(
                            getCurrentUserId(), parametresBtn
                    )
            );
        }

        if (calendrierbtn != null) {
            calendrierbtn.setOnAction(this::openCalendar);
        }

        if (tachesbtn != null) {
            tachesbtn.setOnAction(this::reloadTasksPage);
        }

        loadTasksForUser();
    }

    @Override
    protected boolean checkPagePermissions() {
        return !getSession().isAdmin();
    }

    private void loadTasksForUser() {
        int userId = getCurrentUserId();
        TacheDAO dao = new TacheDAO();

        try {
            List<Tache> result = dao.findByUserId(userId);
            tasks.setAll(result);
            updateCards(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCards(List<Tache> result) {
        for (int i = 0; i < 4; i++) {
            if (i < result.size()) {
                setCard(i, result.get(i));
            } else {
                clearCard(i);
            }
        }
    }

    private void setCard(int index, Tache t) {
        Label projetLabel = projetLabels.get(index);
        Label taskLabel = taskLabels.get(index);
        Label dateLabel = dateLabels.get(index);

        projetLabel.setText(t.getProjetNom() != null ? t.getProjetNom() : "-");
        taskLabel.setText(t.getNom() != null ? t.getNom() : "-");
        dateLabel.setText(
                t.getDateEcheance() != null ? t.getDateEcheance().toString() : "-"
        );

        Node parent = projetLabel.getParent();
        if (parent instanceof VBox box) {
            box.getChildren().removeIf(node -> node instanceof Button);

            Button access = new Button("Accéder");
            access.setStyle(
                    "-fx-background-radius:6; -fx-background-color:#3b82f6; -fx-text-fill:white;"
            );
            access.setOnAction(e -> openTask(t));
            box.getChildren().add(access);
        }
    }

    private void clearCard(int index) {
        projetLabels.get(index).setText("-");
        taskLabels.get(index).setText("-");
        dateLabels.get(index).setText("-");

        Node parent = projetLabels.get(index).getParent();
        if (parent instanceof VBox box) {
            box.getChildren().removeIf(node -> node instanceof Button);
        }
    }

    public void openTask(Tache t) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ViewTacheUser.fxml")
            );
            Parent root = loader.load();

            ViewTacheUsercontroller ctrl = loader.getController();
            ctrl.setTask(t);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Tâche - " + t.getNom());
            stage.setScene(new Scene(root));
            stage.sizeToScene();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openCalendar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fr/ece/javaprojetfinal/Calendar.fxml")
            );

            Scene newScene = new Scene(root, 1000, 700);
            Scene oldScene = calendrierbtn.getScene();
            if (oldScene != null) {
                newScene.getStylesheets().addAll(oldScene.getStylesheets());
            }

            Stage stage = new Stage();
            stage.setTitle("Mon Calendrier");
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            Alert err = new Alert(
                    Alert.AlertType.ERROR,
                    "Impossible d'ouvrir le calendrier.",
                    ButtonType.OK
            );
            err.showAndWait();
        }
    }

    private void reloadTasksPage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fr/ece/javaprojetfinal/HomeUsertaches.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            Scene oldScene = stage.getScene();
            Scene newScene = new Scene(root);
            if (oldScene != null) {
                newScene.getStylesheets().addAll(oldScene.getStylesheets());
            }

            stage.setScene(newScene);
            stage.setTitle("Mes tâches - " + getCurrentUsername());
            stage.setMaximized(true);
        } catch (IOException ex) {
            Alert err = new Alert(
                    Alert.AlertType.ERROR,
                    "Impossible de recharger la page.",
                    ButtonType.OK
            );
            err.showAndWait();
        }
    }
}
