package fr.ece.javaprojetfinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class LoginApplication extends Application {

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
}
