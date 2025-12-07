module JavaProjetFinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.calendarfx.view;

    opens fr.ece.javaprojetfinal to javafx.fxml;

    exports fr.ece.javaprojetfinal;
    exports fr.ece.javaprojetfinal.basics;
    opens fr.ece.javaprojetfinal.basics to javafx.fxml;

}
