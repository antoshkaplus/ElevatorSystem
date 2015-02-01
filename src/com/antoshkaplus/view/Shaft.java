package com.antoshkaplus.view;

import com.antoshkaplus.model.BuildingElevator;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.Console;

/**
 * Created by antoshkaplus on 1/25/15.
 */
public class Shaft extends StackPane {
    private int floorCount;
    private Region[] floors;

    private Background highlightBg = new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), null, null));
    private Background normalBg = new Background(new BackgroundFill(Color.LIGHTGREEN, null, null));

    public Shaft(int floorCount) {
        super();
        this.floorCount = floorCount;

        setOnMouseClicked(e -> {
            System.out.println(this.getBoundsInLocal());
            System.out.println(this.getBoundsInParent());
        });

        //setPadding(new Insets(0, 0, 0, 0));
        VBox v = new VBox();
        getChildren().add(v);
        floors = new Region[floorCount];
        for (int i = 0; i  < floorCount; ++i) {
            Region r = floors[floorCount - i - 1] = new Region();
            v.getChildren().add(r);
            v.setVgrow(r, Priority.ALWAYS);
        }
        for (int i = 0; i < floorCount; ++i) {
            setHighlight(i, false);
        }
    }

    // cast for all elements
    public void setHighlight(boolean highlight) {
        Background bg = getBackground(highlight);
        for (Region r : floors) {
            r.setBackground(bg);
        }
    }

    public void setHighlight(Iterable<Integer> floors, boolean highlight) {
        Background bg = getBackground(highlight);
        for (Integer f : floors) {
            this.floors[f].setBackground(bg);
        }
    }

    public void setHighlight(int floor, boolean highlight) {
        floors[floor].setBackground(getBackground(highlight));
    }

    private Background getBackground(boolean h) {
        return h ? highlightBg : normalBg;
    }

    public int getFloorCount() {
        return floorCount;
    }
}
