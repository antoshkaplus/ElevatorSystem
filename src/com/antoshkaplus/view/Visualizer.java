package com.antoshkaplus.view;

import com.antoshkaplus.model.Elevator.StateListener;
import com.antoshkaplus.model.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.Node;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by antoshkaplus on 1/25/15.
 */
public class Visualizer extends GridPane
                        implements  BuildingElevator.ControlsListener,
                                    StateListener,
                                    Building.Listener {

    private static final int numberWidth = 3;

    private Random random = new Random();
    private Building building;
    private Population population;
    private Map<Integer, Elevator> elevatorViews = new TreeMap<>();

    private EnumMap<Direction, Label[]> waitingLabels;
    private Label[] workingLabels;
    private static final Point2D arrowSize = new Point2D(20, 20);
    private Label[] travelingLabels;
    private Shaft[] shafts;

    public Visualizer(Building building, Population population) {
        super();
        int floorCount = building.getFloorCount();
        int elevatorCount = building.getElevatorCount();
        this.building = building;
        this.population = population;


        building.addListener(this);

        for (BuildingElevator b : building.getElevators()) {
            b.addStateListener(this);
            elevatorViews.put(b.getElevatorId(), new Elevator());
            b.addControlsListener(this);
        }

        // because order of labels in array should be reversed comparing
        // to adding order we can reverse arrays or compute index of array right or
        // reverse array of labels afterwards

        waitingLabels = new EnumMap<>(Direction.class);
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
            add(v, elevatorCount + 1, floorCount - 1 - i);
        }

        workingLabels = new Label[floorCount];
        for (int i = 0; i < floorCount; ++i) {
            add(workingLabels[i] = new Label(), elevatorCount + 2, floorCount - 1 - i);
        }

        travelingLabels = new Label[elevatorCount];
        for (int i = 0; i < elevatorCount; ++i) {
            add(travelingLabels[i] = new Label(), i+1, floorCount);
        }

        shafts = new Shaft[elevatorCount];
        Iterator<Elevator> els = elevatorViews.values().iterator();
        for (int i = 0; i < elevatorCount; ++i) {
            shafts[i] = new Shaft(floorCount);
            shafts[i].getChildren().add(els.next());
            add(shafts[i], i+1, 0, 1, floorCount);
        }

        // floor indexes
        for (int i = 0; i < floorCount; ++i) {
            Label a = new Label(Integer.toString(i + 1));
            a.setStyle("-fx-background-color: blue; -fx-alignment: center;");
            add(a, 0, i);
        }

        // expanding floors to take all available space
        for (int i = 0; i < floorCount; ++i) {
            RowConstraints c = new RowConstraints();
            c.setVgrow(Priority.ALWAYS);
            c.setFillHeight(true);
            getRowConstraints().add(c);
        }

        // expanding shafts
        getColumnConstraints().add(new ColumnConstraints());
        for (int i = 0; i < elevatorCount; ++i) {
            ColumnConstraints c = new ColumnConstraints();
            c.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(c);
        }


        setGridLinesVisible(true);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Visualizer.this.updateElevators();
                Visualizer.this.updatePopulation();
            }
        }.start();
    }

    @Override
    public void onDirectionChange(BuildingElevator elevator) {

    }

    @Override
    public void onHighlightButtonChange(BuildingElevator elevator, int floor, boolean highlight) {

    }

    public static String padNumberLeft(int number) {
        return String.format("%3d", number);
    }

    private void updateElevators() {
        for (BuildingElevator b : building.getElevators()) {
            Elevator v = elevatorViews.get(b.getElevatorId());
            v.setFloor(b.getFloorLocation());
            v.setOpenDoorsPortion(b.getDoorOpenPortion());
        }
    }

    private void updatePopulation() {
        for (int i = 0; i < building.getFloorCount(); ++i) {
            workingLabels[i].setText(padNumberLeft(population.getIdleUserCount(i)));
            for (Map.Entry<Direction, Label[]> entry : waitingLabels.entrySet()) {
                waitingLabels.get(entry.getKey())[i].setText(padNumberLeft(population.getWaitingUserCount(i, entry.getKey())));
            }
        }
        Iterator<Integer> it = elevatorViews.keySet().iterator();
        for (int i = 0; i < building.getElevatorCount(); ++i) {
            travelingLabels[i].setText(padNumberLeft(population.getTravelingUserCount(it.next())));
        }
    }

    @Override
    public void onStateStart(com.antoshkaplus.model.Elevator elevator, com.antoshkaplus.model.Elevator.State state) {
        if (state != com.antoshkaplus.model.Elevator.State.PASSENGER_TRANSFER) {
            return;
        }
        BuildingElevator b = (BuildingElevator)elevator;
        Direction dir = b.getControls().getDirection();

        Platform.runLater(() -> {
            Arrow a = (Arrow)waitingLabels.get(dir)[b.getFloor()].getGraphic();
            a.setHighlight(false);
        });

    }

    @Override
    public void onStateFinish(com.antoshkaplus.model.Elevator elevator, com.antoshkaplus.model.Elevator.State state) {

    }

    @Override
    public void onRequest(Building building, ElevatorRequest request) {
        Platform.runLater(() -> {
            Arrow a = (Arrow)waitingLabels.get(request.direction)[request.floor].getGraphic();
            a.setHighlight(true);
        });
    }
}
