package org.imperial2metric;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Parent root = FXMLLoader.load(classLoader.getResource("Imperial2Metric.fxml"));
        primaryStage.setTitle("Imperial2Metric");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
