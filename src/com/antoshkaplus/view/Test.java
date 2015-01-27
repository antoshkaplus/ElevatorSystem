package com.antoshkaplus.view;

import com.antoshkaplus.model.*;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.Node;

import java.util.*;

/**
 * Created by antoshkaplus on 1/25/15.
 */
public class Test extends GridPane implements BuildingElevator.ControlsListener {

    private Random random = new Random();
    private Building building;
    private Population population;
    private Map<Integer, Elevator> elevatorViews = new TreeMap<>();
    private EnumMap<Direction, Label[]> waitingLabels;
    private Label[] workingLabels;
    private static final Point2D arrowSize = new Point2D(20, 20);
    private Label[] travelingLabels;
    private Shaft[] shafts;


    public Test(Building building, Population population) {
        super();
        int floorCount = building.getFloorCount();
        int elevatorCount = building.getElevatorCount();
        this.building = building;
        this.population = population;

        for (BuildingElevator b : building.getElevators()) {
            elevatorViews.put(b.getElevatorId(), new Elevator());
            b.addControlsListener(this);
        }

        Label[] waiting;
        waiting = new Label[building.getFloorCount()];
        waitingLabels.put(Direction.UP, waiting);
        waiting = new Label[building.getFloorCount()];
        waitingLabels.put(Direction.DOWN, waiting);
        for (int i = 0; i < floorCount; ++i) {
            Label up = waitingLabels.get(Direction.UP)[i] = new Label();
            up.setGraphic(new Arrow(arrowSize.getX(), arrowSize.getY(), Arrow.Type.UP));
            Label down = waitingLabels.get(Direction.DOWN)[i] = new Label();
            down.setGraphic(new Arrow(arrowSize.getX(), arrowSize.getY(), Arrow.Type.DOWN));
            VBox v = new VBox(up, down);
            VBox.setVgrow(up, Priority.ALWAYS);
            VBox.setVgrow(down, Priority.ALWAYS);
            up.setMaxHeight(Double.MAX_VALUE);
            down.setMaxHeight(Double.MAX_VALUE);
            add(v, elevatorCount + 1, i);
        }

        workingLabels = new Label[floorCount];
        for (int i = 0; i < floorCount; ++i) {
            add(workingLabels[i] = new Label(), elevatorCount + 2, i);
        }

        travelingLabels = new Label[elevatorCount];
        for (int i = 0; i < elevatorCount; ++i) {
            add(travelingLabels[i] = new Label(), i+1, floorCount);
        }

        shafts = new Shaft[elevatorCount];
        for (int i = 0; i < elevatorCount; ++i) {
            shafts[i] = new Shaft(floorCount);
            
            Elevator e = new Elevator();
            s.getChildren().add(e);
            s.setStyle("-fx-background-color: yellow;");
            add(s, c, 0, 1, floorCount);
        }


        setGridLinesVisible(true);
        for (int i = 0; i < floorCount; ++i) {
            // floor indexes
            Label a = new Label(Integer.toString(i + 1));
            a.setStyle("-fx-background-color: blue; -fx-alignment: center;");
            add(a, 0, i);
            RowConstraints c = new RowConstraints();
            c.setVgrow(Priority.ALWAYS);
            c.setFillHeight(true);
            getRowConstraints().add(c);
            // number of people on floor

        }
        for (int c = 0; c < elevatorCount+3; ++c) {
            Label a = new Label("0");
            add(a, c, floorCount);
            ColumnConstraints cc = new ColumnConstraints();
            if (c != 0 && c < elevatorCount + 3 - 2) cc.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(c, cc);
        }

        setStyle("-fx-border-color: black;");
    }

    @Override
    public void onDirectionChange(BuildingElevator elevator) {

    }

    @Override
    public void onHighlightButtonChange(BuildingElevator elevator, int floor) {

    }

    private void updateElevators() {
        for (BuildingElevator b : building.getElevators()) {
            Elevator v = elevatorViews.get(b.getElevatorId());
            v.setFloor(b.getFloorLocation());
            v.setOpenDoorsPortion(b.getDoorOpenPortion());
        }
    }

    private void updatePopulation() {

    }

}
