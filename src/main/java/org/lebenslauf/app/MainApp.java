package org.lebenslauf.app;

import org.lebenslauf.manager.DBConnection;
import org.lebenslauf.model.User;
import org.lebenslauf.ui.AuthController;
import org.lebenslauf.ui.ResumeController;
import org.lebenslauf.service.UserService;
import org.lebenslauf.service.ResumeService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static UserService userService;
    private static ResumeService resumeService;
    private static DBConnection dbConnection;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        dbConnection = new DBConnection();
        dbConnection.connect();

        userService = new UserService(dbConnection);
        resumeService = new ResumeService(dbConnection);

        loadScene("auth_layout.fxml", "Login/Register", null);

        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.show();
    }

    public static void loadScene(String fxmlFile, String title, User user) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/lebenslauf/fxml/" + fxmlFile));

        if(fxmlFile.contains("auth_layout")){
            // Set the controller with the DBConnect instance
            AuthController controller = new AuthController(userService, resumeService);
            loader.setController(controller);
        } else if(fxmlFile.contains("main_layout")){
            ResumeController controller = new ResumeController(userService, resumeService, user);
            loader.setController(controller);
        }

        Scene scene;
        if (fxmlFile.contains("main_layout")) {
            scene = new Scene(loader.load(), 1000, 700);
        } else {
            scene = new Scene(loader.load(), 600, 600);
        }
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
    }

    @Override
    public void stop() {
        if (dbConnection != null) {
            dbConnection.disconnect();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
