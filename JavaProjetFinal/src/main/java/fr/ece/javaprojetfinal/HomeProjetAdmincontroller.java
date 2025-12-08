package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

public class HomeProjetAdmincontroller {
    @FXML
    private Label usernameSpot;

    @FXML
    private TableView<Projet> projectsTable;

    @FXML
    private TableColumn<Projet, String> nameCol;

    @FXML
    private TableColumn<Projet, String> descCol;

    @FXML
    private TableColumn<Projet, Projet> actionsCol;

    private final ObservableList<Projet> projects = FXCollections.observableArrayList();

    /**
     * Call this after loading FXML.
     * If isAdmin==true the controller will load projects where the user is the Responsable.
     * If isAdmin==false it will load projects from utilisateurs_projet (membership).
     */
    public void setUser(int userId, String username, boolean isAdmin) {
        if (usernameSpot != null) {
            usernameSpot.setText(username);
        }
        loadProjectsByRole(userId, isAdmin);
    }

    private void loadProjectsByRole(int userId, boolean isAdmin) {
        ProjetDAO dao = new ProjetDAO();
        try {
            List<Projet> projets;
            if (isAdmin) {
                System.out.println("[HomeProjetAdmincontroller] loading projects where user is Responsable -> " + userId);
                projets = dao.findByResponsableId(userId);
            } else {
                System.out.println("[HomeProjetAdmincontroller] loading projects by membership -> " + userId);
                projets = dao.findByUserId(userId);
            }
            final List<Projet> finalProjets = projets;
            Platform.runLater(() -> {
                projects.setAll(finalProjets);
                if (projectsTable != null) {
                    projectsTable.setItems(projects);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // safeguard if FXML still had ListView: ignore it and configure table if present
        if (projectsTable != null) {
            projectsTable.setPlaceholder(new Label("Aucun projet pour cet utilisateur"));

            nameCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNom()));
            descCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getDescription()));

            actionsCol.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
            actionsCol.setCellFactory(col -> new ActionsCell(this));

            projectsTable.setItems(projects);
        }
    }

    // called by ActionsCell
    public void openProject(Projet projet) {
        // TODO: open project details view
        System.out.println("Open project: " + projet.getNom());
    }

    // called by ActionsCell
    public void deleteProject(Projet projet) {
        ProjetDAO dao = new ProjetDAO();
        try {
            // adjust method name if your DAO uses a different API (e.g. delete(projet) or remove(id))
            dao.deleteById(projet.getId());
            Platform.runLater(() -> {
                projects.remove(projet);
            });
            System.out.println("Deleted project: " + projet.getNom());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodError ex) {
            // fallback if your DAO doesn't have deleteById: remove locally and log
            projects.remove(projet);
            System.out.println("Removed locally (DAO delete method not found) : " + projet.getNom());
        }
    }
}
