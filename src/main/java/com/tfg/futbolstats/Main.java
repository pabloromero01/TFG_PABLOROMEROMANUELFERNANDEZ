package com.tfg.futbolstats;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());

        primaryStage.setTitle("FútbolStats — Iniciar sesión");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(520);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
