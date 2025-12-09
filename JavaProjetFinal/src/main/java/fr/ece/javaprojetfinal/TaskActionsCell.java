package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Tache;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class TaskActionsCell extends TableCell<Tache, Tache> {
    private final HBox container = new HBox(8);
    private final Button openBtn = new Button("Accéder");
    private final HomeUserTacheController controller;

    public TaskActionsCell(HomeUserTacheController controller) {
        this.controller = controller;
        container.setPadding(new Insets(4, 4, 4, 4));
        openBtn.setStyle("-fx-background-radius:6; -fx-background-color: #3b82f6; -fx-text-fill: white;");
        container.getChildren().addAll(openBtn);

        openBtn.setOnAction(e -> {
            Tache t = getItem();
            if (t != null) controller.openTask(t);
        });
    }

    @Override
    protected void updateItem(Tache item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(container);
        }
    }
}
