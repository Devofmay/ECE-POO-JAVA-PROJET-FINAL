package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Tache;
import fr.ece.javaprojetfinal.basics.UtilisateurDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import static java.util.function.Predicate.isEqual;

public class ViewTacheUsercontroller extends BaseController{
    @FXML
    private Label nameLabel;
    @FXML
    private Label projectLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private TextArea descriptionArea;

    // newly added labels
    @FXML
    private Label responsableLabel;
    @FXML
    private Label statutLabel;
    @FXML
    private Label prioriteLabel;

    // Called by HomeUserTacheController after loading FXML
    public void setTask(Tache t) {
        if (t == null) return;
        Platform.runLater(() -> {
            nameLabel.setText(t.getNom() != null ? t.getNom() : "-");
            projectLabel.setText(t.getProjetNom() != null ? t.getProjetNom() : "-");
            dateLabel.setText(t.getDateEcheance() != null ? t.getDateEcheance().toString() : "-");

            // populate the new fields - adjust getter names if your Tache class differs
            // If responsable is stored as a user ID, resolve it via UtilisateurDAO
            String responsableDisplay = null;
            try {
                // Try to parse responsable as an int id
                String maybeId = null;
                try {
                    // first try ownerName field (string) if filled
                    maybeId = t.getResponsable();
                } catch (Exception ignored) { }

                if (maybeId != null) {
                    // if it's numeric, lookup name
                    try {
                        int uid = Integer.parseInt(maybeId);
                        UtilisateurDAO udao = new UtilisateurDAO();
                        String name = udao.findNameById(uid);
                        responsableDisplay = name != null ? name : maybeId;
                    } catch (NumberFormatException nfe) {
                        // not a number -> show as-is
                        responsableDisplay = maybeId;
                    }
                } else {
                    responsableDisplay = "-";
                }
            } catch (Exception e) {
                responsableDisplay = "-";
            }
            responsableLabel.setText(responsableDisplay);

            try {
                statutLabel.setText(t.getStatut() != null ? t.getStatut() : "-");
            } catch (Exception ignored) {
                statutLabel.setText("-");
            }
            try {
                prioriteLabel.setText(t.getPriorite() != null ? t.getPriorite() : "-");
            } catch (Exception ignored) {
                prioriteLabel.setText("-");
            }

            String desc = "";
            try {
                desc = t.getDescription() != null ? t.getDescription() : "";
            } catch (Exception ignored) {
            }
            descriptionArea.setText(desc);
            descriptionArea.setEditable(false);
        });
    }

    public void closeWindow(ActionEvent actionEvent) {
        javafx.scene.Node source = (javafx.scene.Node) actionEvent.getSource();
        javafx.stage.Window window = source.getScene().getWindow();
        if (window instanceof javafx.stage.Stage) {
            ((javafx.stage.Stage) window).close();
        } else if (window != null) {
            window.hide();
        }
    }
}
