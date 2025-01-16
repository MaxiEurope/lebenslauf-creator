module org.example.lebenslaufcreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.logging;

    opens org.lebenslauf.ui to javafx.fxml;
    exports org.lebenslauf.app;
}