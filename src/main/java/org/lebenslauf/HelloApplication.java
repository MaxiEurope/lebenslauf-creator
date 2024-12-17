package org.lebenslauf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.backend.DBConnect;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        DBConnect dbConnect = new DBConnect();
        dbConnect.connect();


        loadScene("/org/lebenslauf/fxml/auth_layout.fxml", dbConnect);

        stage.setTitle("Login/Register");

        /*FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("auth_layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Lebenslauf GUI");
        stage.setScene(scene);*/
        stage.show();
    }

    public static void loadScene(String fxmlFile, DBConnect dbConnect) throws Exception {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));

        if(fxmlFile.contains("auth_layout")){
            // Set the controller with the DBConnect instance
            LoginRegisterController controller = new LoginRegisterController(dbConnect);
            loader.setController(controller);
        } else if(fxmlFile.contains("main_layout")){
            MainController controller = new MainController(dbConnect);
            loader.setController(controller);
        }

        Scene scene = new Scene(loader.load(), 600, 600);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();

    }
}
