package org.example.budgetinsight.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.budgetinsight.HelloApplication;

import java.io.IOException;

public class SceneNavigator {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1200, 750);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Navigare imposibila catre " + fxmlFile, e);
        }
    }
}