package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class ActionsCell extends TableCell<Projet, Projet> {
    private final HBox container = new HBox(8);
    private final Button openBtn = new Button("Accéder");
    private final Button deleteBtn = new Button("Supprimer");
    private final HomeProjetAdmincontroller controller;

    public ActionsCell(HomeProjetAdmincontroller controller) {
        this.controller = controller;
        container.setPadding(new Insets(4, 4, 4, 4));
        openBtn.setStyle("-fx-background-radius:6; -fx-background-color: #3b82f6; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-radius:6; -fx-background-color: #ef4444; -fx-text-fill: white;");
        container.getChildren().addAll(openBtn, deleteBtn);

        openBtn.setOnAction(e -> {
            Projet p = getItem();
            if (p != null) controller.openProject(p);
        });
        deleteBtn.setOnAction(e -> {
            Projet p = getItem();
            if (p != null) controller.deleteProject(p);
        });
    }

    @Override
    protected void updateItem(Projet item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(container);
        }
    }
}