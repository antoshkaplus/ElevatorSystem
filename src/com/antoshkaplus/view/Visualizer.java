package com.antoshkaplus.view;

import com.antoshkaplus.model.Elevator.StateListener;
import com.antoshkaplus.model.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.omg.PortableServer.POA;

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
    private static final Font labelFont = new Font("Courier", 14);

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
        shafts = new Shaft[elevatorCount];
        Iterator<Elevator> els = elevatorViews.values().iterator();
        for (int i = 0; i < elevatorCount; ++i) {
            shafts[i] = new Shaft(floorCount);
            shafts[i].getChildren().add(els.next());
            add(shafts[i], i+1, 0);
        }

        initFloorNumbers();
        initWaitingLabels();
        initWorkingLabels();

        travelingLabels = new Label[elevatorCount];
        for (int i = 0; i < elevatorCount; ++i) {
            Label a = travelingLabels[i] = new Label();
            a.setFont(labelFont);
            a.setPadding(new Insets(5));
            a.setMaxWidth(Double.MAX_VALUE);
            a.setAlignment(Pos.CENTER);
            add(a, i + 1, 1);
        }

        // for the first row
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setFillHeight(true);
        rowConstraints.setVgrow(Priority.ALWAYS);
        getRowConstraints().add(rowConstraints);

        // expanding shafts
        getColumnConstraints().add(new ColumnConstraints());
        for (int i = 0; i < elevatorCount; ++i) {
            ColumnConstraints c = new ColumnConstraints();
            c.setHgrow(Priority.ALWAYS);
            c.setFillWidth(true);
            getColumnConstraints().add(c);
        }

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Visualizer.this.updateElevators();
                Visualizer.this.updatePopulation();
            }
        }.start();
    }

    private void initFloorNumbers() {
        VBox v = new VBox();
        // floor indexes
        int floorCount = building.getFloorCount();
        for (int i = 0; i < floorCount; ++i) {
            Label a = new Label(Integer.toString(floorCount - i));
            a.setPadding(new Insets(5));
            a.setMaxHeight(Double.MAX_VALUE);
            v.getChildren().add(a);
            VBox.setVgrow(a, Priority.ALWAYS);
        }
        add(v, 0, 0);
    }

    private void initWorkingLabels() {
        int floorCount = building.getFloorCount();
        int elevatorCount = building.getElevatorCount();
        VBox v = new VBox();
        workingLabels = new Label[floorCount];
        for (int i = 0; i < floorCount; ++i) {
            Label a = workingLabels[floorCount - 1 - i] = new Label();
            a.setFont(labelFont);
            a.setMaxHeight(Double.MAX_VALUE);
            a.setPadding(new Insets(5));
            v.getChildren().add(a);
            VBox.setVgrow(a, Priority.ALWAYS);
        }
        v.setMaxHeight(Double.MAX_VALUE);
        add(v, elevatorCount + 2, 0);
    }

    private void initWaitingLabels() {
        // because order of labels in array should be reversed comparing
        // to adding order we can reverse arrays or compute index of array right or
        // reverse array of labels afterwards
        int floorCount = building.getFloorCount();
        int elevatorCount = building.getElevatorCount();
        waitingLabels = new EnumMap<>(Direction.class);
        waitingLabels.put(Direction.UP, new Label[floorCount]);
        waitingLabels.put(Direction.DOWN, new Label[floorCount]);
        GridPane g = new GridPane();
        for (int i = 0; i < floorCount; ++i) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(100./floorCount);
            g.getRowConstraints().add(r);

            VBox v = new VBox();
            v.setMaxHeight(Double.MAX_VALUE);
            v.setPadding(new Insets(5));

            Label up = waitingLabels.get(Direction.UP)[i] = new Label();
            up.setFont(labelFont);
            up.setGraphic(new Arrow(arrowSize.getX(), arrowSize.getY(), Arrow.Type.UP));
            up.setMaxHeight(Double.MAX_VALUE);
            if (i != floorCount-1) {
                v.getChildren().add(up);
                VBox.setVgrow(up, Priority.ALWAYS);
            }
            Label down = waitingLabels.get(Direction.DOWN)[i] = new Label();
            down.setFont(labelFont);
            down.setGraphic(new Arrow(arrowSize.getX(), arrowSize.getY(), Arrow.Type.DOWN));
            down.setMaxHeight(Double.MAX_VALUE);
            if (i != 0) {
                v.getChildren().add(down);
                VBox.setVgrow(down, Priority.ALWAYS);
            }
            g.add(v, 0, floorCount - 1 - i);
        }
        add(g, elevatorCount + 1, 0);
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
        int floorCount = building.getFloorCount();
        int i = 0;
        for (BuildingElevator b : building.getElevators()) {
            Elevator v = elevatorViews.get(b.getElevatorId());
            v.setFloor(b.getFloorLocation());
            v.setOpenDoorsPortion(b.getDoorOpenPortion());
            shafts[i].setHighlight(false);
            shafts[i].setHighlight(building.getElevatorStops(b), true);
            ++i;
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
