package fr.ece.javaprojetfinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/javaprojetfinal/Calendar.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Mon Calendrier");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
