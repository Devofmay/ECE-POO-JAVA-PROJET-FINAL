module JavaProjetFinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;


    opens fr.ece.javaprojetfinal to javafx.fxml;

    exports fr.ece.javaprojetfinal;

}
