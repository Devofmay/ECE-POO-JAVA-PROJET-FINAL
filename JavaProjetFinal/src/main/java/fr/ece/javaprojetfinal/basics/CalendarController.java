package fr.ece.javaprojetfinal.basics;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import fr.ece.javaprojetfinal.BaseController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CalendarController extends BaseController {

    @FXML
    private AnchorPane calendarContainer;

    @FXML
    private Label usernameSpot;

    private CalendarView calendarView;
    private Calendar planningCalendar;
    private Calendar tasksCalendar; // Calendrier pour les tâches

    private Map<String, Integer> eventIdMap = new HashMap<>();

    private int currentAdminId = 1; // TODO: Récupérer l'ID réel de l'admin connecté

    @Override
    protected boolean checkPagePermissions() {
        // si tu veux restreindre le calendrier à l’admin :
        return getSession().isAdmin();
        // ou, pour l’ouvrir à tout le monde :
        // return true;
    }

    @FXML
    public void initialize() {
        initializeSession();
        // Test de connexion
        testDatabaseConnection();

        // Créer le CalendarView
        calendarView = new CalendarView();

        // Calendrier des événements
        planningCalendar = new Calendar("Planning Administration");
        planningCalendar.setStyle(Calendar.Style.STYLE1);
        planningCalendar.setShortName("Admin");

        // Calendrier des tâches
        tasksCalendar = new Calendar("Tâches");
        tasksCalendar.setStyle(Calendar.Style.STYLE2); // Couleur différente
        tasksCalendar.setShortName("Tâches");

        // Source de calendriers
        CalendarSource calendarSource = new CalendarSource("Mes Calendriers");
        calendarSource.getCalendars().addAll(planningCalendar, tasksCalendar);
        calendarView.getCalendarSources().add(calendarSource);

        // Configuration du CalendarView
        calendarView.setRequestedTime(LocalTime.now());
        calendarView.setToday(LocalDate.now());
        calendarView.setTime(LocalTime.now());
        calendarView.showMonthPage();

        // Création d'événements par double-clic
        calendarView.setEntryFactory(param -> {
            Entry<String> entry = new Entry<>("Nouvel événement");
            entry.setInterval(param.getZonedDateTime(), param.getZonedDateTime().plusHours(1));
            entry.setCalendar(planningCalendar);

            showEntryDialog(entry, true);
            return entry;
        });

        // Modifier un événement existant
        calendarView.setEntryDetailsCallback(param -> {
            Entry<?> entry = param.getEntry();
            showEntryDialog(entry, false);
            return true;
        });

        // Glisser-déposer des événements
        calendarView.setEntryEditPolicy(param -> {
            saveEventToDatabase(param.getEntry(), false);
            return true;
        });

        // Ancrer le calendrier
        anchorToParent(calendarView);
        calendarContainer.getChildren().add(calendarView);

        // Charger événements et tâches
        loadEventsFromDatabase();
        loadTasksFromDatabase();
    }

    private void testDatabaseConnection() {
        try (Connection conn = DBconnect.getConnection()) {
            System.out.println("✅ Connexion à la base de données réussie !");
            System.out.println("Base de données : " + conn.getCatalog());

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'events'");
            if (rs.next()) {
                System.out.println("✅ Table 'events' trouvée !");
            } else {
                System.out.println("❌ Table 'events' introuvable !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void anchorToParent(CalendarView view) {
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
    }

    private void showEntryDialog(Entry<?> entry, boolean isNew) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isNew ? "Créer un événement" : "Modifier l'événement");
        dialog.setHeaderText("Détails de l'événement");

        ButtonType saveButton = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        ButtonType deleteButton = null;
        if (!isNew) {
            deleteButton = new ButtonType("Supprimer", ButtonBar.ButtonData.OTHER);
            dialog.getDialogPane().getButtonTypes().add(deleteButton);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(entry.getTitle());
        titleField.setPromptText("Ex: Réunion d'équipe");
        titleField.setPrefWidth(300);

        TextArea descriptionArea = new TextArea(entry.getLocation() != null ? entry.getLocation() : "");
        descriptionArea.setPromptText("Ex: Salle de conférence A");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        DatePicker startDatePicker = new DatePicker(entry.getStartDate());
        TextField startTimeField = new TextField(entry.getStartTime().toString());
        startTimeField.setPromptText("09:00");
        startTimeField.setPrefWidth(80);

        DatePicker endDatePicker = new DatePicker(entry.getEndDate());
        TextField endTimeField = new TextField(entry.getEndTime().toString());
        endTimeField.setPromptText("17:00");
        endTimeField.setPrefWidth(80);

        CheckBox allDayCheckBox = new CheckBox("Toute la journée");
        allDayCheckBox.setSelected(entry.isFullDay());
        allDayCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            startTimeField.setDisable(newVal);
            endTimeField.setDisable(newVal);
        });

        int row = 0;
        grid.add(new Label("Titre *:"), 0, row);
        grid.add(titleField, 1, row, 2, 1);

        row++;
        grid.add(new Label("Description:"), 0, row);
        grid.add(descriptionArea, 1, row, 2, 1);

        row++;
        grid.add(allDayCheckBox, 1, row, 2, 1);

        row++;
        grid.add(new Label("Date de début *:"), 0, row);
        grid.add(startDatePicker, 1, row);
        grid.add(startTimeField, 2, row);

        row++;
        grid.add(new Label("Date de fin *:"), 0, row);
        grid.add(endDatePicker, 1, row);
        grid.add(endTimeField, 2, row);

        dialog.getDialogPane().setContent(grid);
        titleField.requestFocus();

        ButtonType finalDeleteButton = deleteButton;
        Optional<ButtonType> result = dialog.showAndWait();

        result.ifPresent(buttonType -> {
            if (buttonType == finalDeleteButton) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmer la suppression");
                confirmAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet événement ?");
                Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    planningCalendar.removeEntry(entry);
                    deleteEventFromDatabase(entry);
                    showAlert("Suppression réussie", "L'événement a été supprimé.", Alert.AlertType.INFORMATION);
                }

            } else if (buttonType == saveButton) {
                if (titleField.getText().trim().isEmpty()) {
                    showAlert("Erreur", "Le titre est obligatoire.", Alert.AlertType.ERROR);
                    if (isNew) planningCalendar.removeEntry(entry);
                    return;
                }

                try {
                    entry.setTitle(titleField.getText().trim());
                    entry.setLocation(descriptionArea.getText().trim());
                    entry.setFullDay(allDayCheckBox.isSelected());

                    if (allDayCheckBox.isSelected()) {
                        entry.setInterval(
                                startDatePicker.getValue().atStartOfDay(),
                                endDatePicker.getValue().atTime(23, 59, 59)
                        );
                    } else {
                        LocalTime startTime = LocalTime.parse(startTimeField.getText().trim());
                        LocalTime endTime = LocalTime.parse(endTimeField.getText().trim());

                        LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTime);
                        LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTime);

                        if (endDateTime.isBefore(startDateTime)) {
                            showAlert("Erreur", "La date de fin doit être après la date de début.", Alert.AlertType.ERROR);
                            if (isNew) planningCalendar.removeEntry(entry);
                            return;
                        }

                        entry.setInterval(startDateTime, endDateTime);
                    }

                    saveEventToDatabase(entry, isNew);
                    showAlert("Succès", "L'événement a été " + (isNew ? "créé" : "modifié") + " avec succès.", Alert.AlertType.INFORMATION);

                } catch (Exception e) {
                    showAlert("Erreur", "Format de date/heure invalide. Utilisez le format HH:mm (ex: 09:30)", Alert.AlertType.ERROR);
                    if (isNew) planningCalendar.removeEntry(entry);
                }

            } else {
                if (isNew) {
                    planningCalendar.removeEntry(entry);
                }
            }
        });
    }

    private void loadEventsFromDatabase() {
        try (Connection conn = DBconnect.getConnection()) {
            String query = "SELECT * FROM events WHERE admin_id = ? ORDER BY start_time";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentAdminId);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                Entry<String> entry = new Entry<>(rs.getString("title"));

                int eventId = rs.getInt("id");
                String entryId = "event_" + eventId;
                entry.setId(entryId);

                eventIdMap.put(entryId, eventId);

                String description = rs.getString("description");
                if (description != null && !description.isEmpty()) {
                    entry.setLocation(description);
                }

                Timestamp startTime = rs.getTimestamp("start_time");
                Timestamp endTime = rs.getTimestamp("end_time");

                entry.setInterval(startTime.toLocalDateTime(), endTime.toLocalDateTime());
                entry.setFullDay(rs.getBoolean("is_full_day"));

                planningCalendar.addEntry(entry);
                count++;
            }
            System.out.println("✅ " + count + " événement(s) chargé(s) depuis la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les événements : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadTasksFromDatabase() {
        try (Connection conn = DBconnect.getConnection()) {
            String query = "SELECT ID, Nom, Description, Date_echeances FROM taches WHERE Date_echeances IS NOT NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                int taskId = rs.getInt("ID");
                String taskName = rs.getString("Nom");
                String taskDesc = rs.getString("Description");
                Date dueDate = rs.getDate("Date_echeances");

                if (dueDate != null) {
                    Entry<String> entry = new Entry<>(taskName);
                    entry.setId("task_" + taskId);
                    entry.setLocation(taskDesc != null ? taskDesc : "");
                    entry.setInterval(dueDate.toLocalDate().atStartOfDay(), dueDate.toLocalDate().atTime(23, 59, 59));
                    entry.setFullDay(true);

                    tasksCalendar.addEntry(entry);
                    count++;
                }
            }
            System.out.println("✅ " + count + " tâche(s) chargée(s) dans le calendrier.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les tâches : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void saveEventToDatabase(Entry<?> entry, boolean isNew) {
        try (Connection conn = DBconnect.getConnection()) {
            if (isNew) {
                String query = "INSERT INTO events (title, description, start_time, end_time, is_full_day, admin_id, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                stmt.setString(1, entry.getTitle());
                stmt.setString(2, entry.getLocation());
                stmt.setTimestamp(3, Timestamp.valueOf(entry.getStartAsLocalDateTime()));
                stmt.setTimestamp(4, Timestamp.valueOf(entry.getEndAsLocalDateTime()));
                stmt.setBoolean(5, entry.isFullDay());
                stmt.setInt(6, currentAdminId);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        String entryId = "event_" + generatedId;
                        entry.setId(entryId);
                        eventIdMap.put(entryId, generatedId);
                        System.out.println("✅ Événement créé : " + entry.getTitle());
                    }
                }
            } else {
                Integer eventId = eventIdMap.get(entry.getId());
                if (eventId == null) return;

                String query = "UPDATE events SET title=?, description=?, start_time=?, end_time=?, is_full_day=?, updated_at=NOW() WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setString(1, entry.getTitle());
                stmt.setString(2, entry.getLocation());
                stmt.setTimestamp(3, Timestamp.valueOf(entry.getStartAsLocalDateTime()));
                stmt.setTimestamp(4, Timestamp.valueOf(entry.getEndAsLocalDateTime()));
                stmt.setBoolean(5, entry.isFullDay());
                stmt.setInt(6, eventId);

                stmt.executeUpdate();
                System.out.println("✅ Événement mis à jour : " + entry.getTitle());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de sauvegarder l'événement : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteEventFromDatabase(Entry<?> entry) {
        try (Connection conn = DBconnect.getConnection()) {
            Integer eventId = eventIdMap.get(entry.getId());
            if (eventId == null) return;

            String query = "DELETE FROM events WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                eventIdMap.remove(entry.getId());
                System.out.println("✅ Événement supprimé : " + entry.getTitle());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer l'événement : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
