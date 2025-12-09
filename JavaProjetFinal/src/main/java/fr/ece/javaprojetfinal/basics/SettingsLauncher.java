package fr.ece.javaprojetfinal.basics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Ouvre la fenêtre des paramètres pour un utilisateur donné.
 */
public class SettingsLauncher {

    /**
     * Ouvre la fenêtre des paramètres pour un utilisateur donné.
     * @param userId id de l'utilisateur
     * @param sourceNode le nœud source pour la modalité (peut être null)
     */
    public static void openParametresForUser(int userId, Node sourceNode) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    SettingsLauncher.class.getResource("/fr/ece/javaprojetfinal/ParametresMonCompte.fxml")));
            Parent root = loader.load();

            // inject user id into controller
            ParametresMonComptecontroller controller = loader.getController();
            controller.setUserId(userId);

            Stage stage = new Stage();
            stage.setTitle("Paramètres - Mon compte");
            if (sourceNode != null && sourceNode.getScene() != null) {
                stage.initOwner(sourceNode.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
            }
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // handle error (optional: show JavaFX Alert)
        }
    }
}
