package fr.ece.javaprojetfinal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CommonController {
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label errorMsg;
    public  void login() throws IOException {



    String username = usernameField.getText();
    String password = passwordField.getText();

        if (username.isEmpty()) {
        errorMsg.setText("Le nom d'utilisateur est obligatoire");
    }
//        else {
    }
}
