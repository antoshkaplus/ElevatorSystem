package com.antoshkaplus;

import com.antoshkaplus.model.*;
import com.antoshkaplus.model.Elevator;
import com.antoshkaplus.model.ElevatorSystem;
import com.antoshkaplus.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by antoshkaplus on 10/16/14.
 */
public class Simulation extends Application {

    // create some button to change building configuration
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Elevator System Visualization");
        Building building = new Building(15, 15);
        Population population = new Population(building, 500, 500);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                population.run();
            }
        });
        thread.start();
        Visualizer elevatorSystem = new Visualizer(building, population);
        primaryStage.setScene(new Scene(elevatorSystem, 1000, 700));
        primaryStage.show();
    }
}
