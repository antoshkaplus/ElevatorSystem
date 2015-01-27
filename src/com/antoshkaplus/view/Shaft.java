package com.antoshkaplus.view;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.*;

import java.io.Console;

/**
 * Created by antoshkaplus on 1/25/15.
 */
public class Shaft extends StackPane {
    private int floorCount;

    public Shaft(int floorCount) {
        super();
        this.floorCount = floorCount;

        setOnMouseClicked(e -> {
            System.out.println(this.getBoundsInLocal());
            System.out.println(this.getBoundsInParent());
        });

        //setPadding(new Insets(0, 0, 0, 0));
        VBox floors = new VBox();
        getChildren().add(floors);
        for (int i = 0; i  < floorCount; ++i) {

            Region r = new Region();

//            r.setOnMouseClicked(e -> {
//                System.out.println(r.getBoundsInLocal());
//                System.out.println(r.getBoundsInParent());
//            });

            String s = "-fx-background-color: " + (i % 2 == 0 ? "green" : "red") + ";";
            r.setStyle(s);
            floors.getChildren().add(r);
            floors.setVgrow(r, Priority.ALWAYS);
        }
    }

    public int getFloorCount() {
        return floorCount;
    }
}
