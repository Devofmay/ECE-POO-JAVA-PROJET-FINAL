package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.Tache;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @FXML
    private TableColumn<Tache, Tache> actionsColumn;

    @FXML
    private Button modifprojet;

    private final ObservableList<Tache> taskNames = FXCollections.observableArrayList();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Projet currentProjet;

    @FXML
    private void initialize() {
        if (tasksTable != null) {
            taskNameColumn.setCellValueFactory(cell -> {
                Tache t = cell.getValue();
                String v = safeGetString(t, new String[]{"getNom", "getTitre", "getTitle", "getName"});
                return new ReadOnlyStringWrapper(v);
            });

            creationDateColumn.setCellValueFactory(cell -> {
                Tache t = cell.getValue();
                String v = safeGetDateString(t, new String[]{"getDateCreation", "getCreationDate", "getDateCreated"});
                return new ReadOnlyStringWrapper(v);
            });

            dueDateColumn.setCellValueFactory(cell -> {
                Tache t = cell.getValue();
                String v = safeGetDateString(t, new String[]{"getDateEcheances", "getDateEcheance", "getDueDate", "getDateDeadline"});
                return new ReadOnlyStringWrapper(v);
            });

            ownerColumn.setCellValueFactory(cell -> {
                Tache t = cell.getValue();
                String v = safeGetString(t, new String[]{"getOwnerName", "getResponsable", "getResponsableName", "getAssignee"});
                return new ReadOnlyStringWrapper(v);
            });

            statusColumn.setCellValueFactory(cell -> {
                Tache t = cell.getValue();
                String v = safeGetString(t, new String[]{"getStatut", "getStatus", "getEtat"});
                return new ReadOnlyStringWrapper(v);
            });

            // Actions column with modify/delete buttons
            if (actionsColumn != null) {
                actionsColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue()));
                actionsColumn.setCellFactory(col -> new TableCell<Tache, Tache>() {
                    private final HBox container = new HBox(8);
                    private final Button modifyBtn = new Button("Modifier");
                    private final Button deleteBtn = new Button("Supprimer");

                    {
                        container.setPadding(new Insets(4));
                        modifyBtn.setStyle("-fx-background-radius:6; -fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size:11;");
                        deleteBtn.setStyle("-fx-background-radius:6; -fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size:11;");
                        container.getChildren().addAll(modifyBtn, deleteBtn);

                        modifyBtn.setOnAction(e -> {
                            Tache t = getTableView().getItems().get(getIndex());
                            if (t != null) modifyTask(t);
                        });

                        deleteBtn.setOnAction(e -> {
                            Tache t = getTableView().getItems().get(getIndex());
                            if (t != null) deleteTask(t);
                        });
                    }

                    @Override
                    protected void updateItem(Tache item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(container);
                        }
                    }
                });
            }

            tasksTable.setItems(taskNames);
        }

        if (collaboratorsList != null) {
            collaboratorsList.getItems().clear();
        }

        if (modifprojet != null) {
            modifprojet.setOnAction(e -> openModifierProjet());
        }
    }

    private void modifyTask(Tache t) {
        try {
            Stage stage = null;
            Scene oldScene = null;

            if (tasksTable != null && tasksTable.getScene() != null && tasksTable.getScene().getWindow() instanceof Stage) {
                stage = (Stage) tasksTable.getScene().getWindow();
                oldScene = tasksTable.getScene();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/ModifierTache.fxml"));
            Parent modifierRoot = loader.load();

            ModifierTachecontroller controller = loader.getController();
            if (controller != null) {
                controller.setTask(t);
                controller.setPreviousScene(oldScene);
                controller.setParentController(this);
            }

            if (stage != null) {
                Scene newScene = new Scene(modifierRoot);
                if (oldScene != null) newScene.getStylesheets().addAll(oldScene.getStylesheets());
                stage.setScene(newScene);
                stage.setTitle("Modifier la tâche - " + t.getNom());
                stage.sizeToScene();
            } else {
                Stage s = new Stage();
                s.setScene(new Scene(modifierRoot));
                s.setTitle("Modifier la tâche - " + t.getNom());
                s.show();
            }

        } catch (IOException ex) {
            System.err.println("Failed to load ModifierTache.fxml: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void deleteTask(Tache t) {
        System.out.println("Delete task: " + t.getNom());
        // TODO: Implement delete task logic
        taskNames.remove(t);
    }

    private String safeGetString(Tache t, String[] getterNames) {
        if (t == null) return "";
        for (String g : getterNames) {
            try {
                Method m = t.getClass().getMethod(g);
                Object val = m.invoke(t);
                if (val != null) return String.valueOf(val);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ex) {
                System.err.println("Reflection error calling " + g + ": " + ex.getMessage());
            }
        }
        return "";
    }

    private String safeGetDateString(Tache t, String[] getterNames) {
        if (t == null) return "";
        for (String g : getterNames) {
            try {
                Method m = t.getClass().getMethod(g);
                Object val = m.invoke(t);
                if (val == null) continue;
                if (val instanceof LocalDate) {
                    return localDateFormatter.format((LocalDate) val);
                } else if (val instanceof java.util.Date) {
                    return dateFormat.format((java.util.Date) val);
                } else {
                    return val.toString();
                }
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ex) {
                System.err.println("Reflection date error calling " + g + ": " + ex.getMessage());
            }
        }
        return "";
    }

    public void setProject(Projet projet) {
        if (projet == null) return;
        this.currentProjet = projet;

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
                System.err.println("DB SQL error loading project details: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Unexpected error loading project details: " + e.getMessage());
                e.printStackTrace();
            }

            if (responsableName == null) responsableName = "";
            for (Tache t : tasks) {
                try {
                    try {
                        Method setOwner = t.getClass().getMethod("setOwnerName", String.class);
                        setOwner.invoke(t, responsableName);
                    } catch (NoSuchMethodException ignored) {
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to set owner on task: " + ex.getMessage());
                }
            }

            if (collaboratorsList != null) {
                collaboratorsList.getItems().setAll(collaborators);
            }

            System.out.println("Loaded tasks from DAO: " + (tasks != null ? tasks.size() : 0));
            taskNames.setAll(tasks);
            System.out.println("Table backing list size: " + taskNames.size());

            if (usernameField != null) {
                usernameField.setText(responsableName);
            }
        });
    }

    private void openModifierProjet() {
        if (currentProjet == null) return;
        try {
            Stage stage = null;
            Scene oldScene = null;
            if (projectNameField != null && projectNameField.getScene() != null && projectNameField.getScene().getWindow() instanceof Stage) {
                stage = (Stage) projectNameField.getScene().getWindow();
                oldScene = projectNameField.getScene();
            } else if (tasksTable != null && tasksTable.getScene() != null && tasksTable.getScene().getWindow() instanceof Stage) {
                stage = (Stage) tasksTable.getScene().getWindow();
                oldScene = tasksTable.getScene();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/ModifierProjet.fxml"));
            Parent modifierRoot = loader.load();

            Object controllerObj = loader.getController();
            if (controllerObj instanceof fr.ece.javaprojetfinal.ModifierProjetcontroller) {
                fr.ece.javaprojetfinal.ModifierProjetcontroller controller = (fr.ece.javaprojetfinal.ModifierProjetcontroller) controllerObj;
                controller.setProject(currentProjet);
                controller.setPreviousScene(oldScene);
                controller.setParentController(this);
            } else {
                if (controllerObj != null) {
                    try {
                        controllerObj.getClass().getMethod("setProject", Projet.class).invoke(controllerObj, currentProjet);
                        controllerObj.getClass().getMethod("setPreviousScene", Scene.class).invoke(controllerObj, oldScene);
                        controllerObj.getClass().getMethod("setParentController", InsideProjetAdminController.class).invoke(controllerObj, this);
                    } catch (NoSuchMethodException ignored) {
                    } catch (Exception ex) {
                        System.err.println("Failed to set modifier controller references: " + ex.getMessage());
                    }
                }
            }

            if (stage != null) {
                Scene newScene = new Scene(modifierRoot);
                if (oldScene != null) newScene.getStylesheets().addAll(oldScene.getStylesheets());
                stage.setScene(newScene);
                stage.setTitle("Modifier le projet - " + currentProjet.getNom());
                stage.sizeToScene();
            } else {
                Stage s = new Stage();
                s.setScene(new Scene(modifierRoot));
                s.setTitle("Modifier le projet - " + currentProjet.getNom());
                s.show();
            }

        } catch (IOException ex) {
            System.err.println("Failed to load ModifierProjet.fxml: " + ex.getMessage());
        }
    }
}
