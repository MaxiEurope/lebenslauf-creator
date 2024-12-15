module org.example.lebenslaufcreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;


    opens org.lebenslauf to javafx.fxml;
    exports org.lebenslauf;
}