module org.example.lebenslaufcreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;


    opens org.lebenslauf to javafx.fxml;
    exports org.lebenslauf;
}