package com.antoshkaplus;


import com.antoshkaplus.view.Test;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by antoshkaplus on 1/25/15.
 */
public class TestVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Elevator System Visualization Test");
        Test test = new Test(12, 4);
        primaryStage.setScene(new Scene(test, 1000, 700));
        primaryStage.show();
    }
}
