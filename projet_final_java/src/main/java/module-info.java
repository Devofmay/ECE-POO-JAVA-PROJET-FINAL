module fr.ece.projet_final_java {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens fr.ece.projet_final_java to javafx.fxml;
    exports fr.ece.projet_final_java;
}