package com.antoshkaplus.view;

import com.antoshkaplus.model.Building;
import com.antoshkaplus.model.Direction;
import com.antoshkaplus.model.ElevatorRequest;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Created by antoshkaplus on 10/23/14.
 *
 * why is it not inherit from group
 * probably because this thing is resizable
 */
public class ElevatorSystem extends Pane {

    private Building building;
    private Canvas canvas;

    private double titleHeight;
    private double floorHeight;
    private double floorWidth;
    private double floorPanelWidth;
    private double floorNumberWidth;

    // use it to query model
    private Map<Integer, Elevator> elevatorViews = new TreeMap<Integer, Elevator>();
    private Map<Integer, Label> elevatorTargetViews = new HashMap<Integer, Label>();

    // index means floor
    private ArrayList<FloorPanel> floorPanels = new ArrayList<FloorPanel>();
    private ArrayList<Label> floorLabels = new ArrayList<Label>();


    public ElevatorSystem(double width, double height, final Building building) {
        super();
        setWidth(width);
        setHeight(height);
        this.building = building;

        // stackPane gridPane
        final int floorCount = building.getFloorCount();
        int elevatorCount = building.getElevatorCount();
        titleHeight = 20;
        floorHeight = (height - titleHeight) / floorCount;
        floorWidth = width / (elevatorCount + 2);
        floorPanelWidth = 50;
        floorNumberWidth = floorWidth;

        int k = 0;
        final Group elevatorTargets = new Group();
        for (com.antoshkaplus.model.Elevator elevator : building.getElevators()) {
//            Elevator elevatorView = new Elevator();
//            elevatorView.setTranslateX(floorNumberWidth + k * floorWidth + floorWidth/2 - 2*floorWidth/5/2);
//            elevatorView.resize(2 * floorWidth / 5, floorHeight / 2);
//            getChildren().add(elevatorView);
//            elevatorViews.put(elevator.getElevatorId(), elevatorView);
//
//            Label label = new Label();
//            label.setTranslateX(floorNumberWidth + (k)*floorWidth);
//            label.setTranslateY(0);
//            this.elevatorTargetViews.put(elevator.getElevatorId(), label);
//            elevatorTargets.getChildren().add(label);
//            k++;
        }
        getChildren().add(elevatorTargets);

        // i == 0 : most upper floor
        // i == floorCount - 1 : most lower floor

        // could just use Group, and remove that list completely

        final Group floorPanels = new Group();
        final Group floorLabels = new Group();
        FloorPanel floorPanel;
        for (int i = 0; i < floorCount; ++i) {
            if (i == 0) floorPanel = new FloorPanel(FloorPanel.ArrowType.DOWN);
            else if (i == floorCount-1) floorPanel = new FloorPanel(FloorPanel.ArrowType.UP);
            else floorPanel = new FloorPanel(FloorPanel.ArrowType.BOTH);
            this.floorPanels.add(floorPanel);
            floorPanel.setArrowCasualColor(Color.GREY);
            floorPanel.setArrowHighlightColor(Color.YELLOW);
            floorPanel.resizeRelocate((elevatorCount + 1) * floorWidth + floorWidth/2 - floorPanelWidth/2, i * floorHeight, floorPanelWidth, floorHeight);
            floorPanels.getChildren().add(floorPanel);

            Label label = new Label(Integer.toString(i+1));
            label.setTranslateX(0);
            label.setTranslateY((floorCount - i - 1) * floorHeight);
            this.floorLabels.add(label);
            floorLabels.getChildren().add(label);
        }
        Collections.reverse(this.floorPanels);
        floorPanels.setTranslateY(titleHeight);
        floorLabels.setTranslateY(titleHeight);


        initCanvas();

        getChildren().add(floorPanels);
        getChildren().add(floorLabels);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                //System.out.println(now);
                // update elevator locations
                // update floor panels
                for (com.antoshkaplus.model.Elevator elevator : building.getElevators()) {
                    Elevator elevatorView = elevatorViews.get(elevator.getElevatorId());
                   // elevatorView.setOpenPortion(elevator.getDoorOpenPortion());
                    double floor = elevator.getFloorLocation();
                    elevatorView.setTranslateY((floorCount - 1 - floor) * floorHeight + floorHeight / 2 - floorHeight / 4 + titleHeight);

                    elevatorTargetViews.get(elevator.getElevatorId()).setText(Integer.toString(elevator.getDestination()));
                }

                List<FloorPanel> floorPanels = ElevatorSystem.this.floorPanels;
                for (int i = 0; i < floorCount; ++i) {
//                    if (building.isElevatorRequested(new ElevatorRequest(i, Direction.UP))) {
//                        if (i == floorCount -1) throw new RuntimeException("LOH UP");
//                        floorPanels.get(i).setHighlightArrow(FloorPanel.ArrowType.UP, true);
//                    } else {
//                        if (i != floorCount -1) floorPanels.get(i).setHighlightArrow(FloorPanel.ArrowType.UP, false);
//                    }
//                    if (building.isElevatorRequested(new ElevatorRequest(i, Direction.DOWN))) {
//                        if (i == 0) throw new RuntimeException("LOH DOWN");
//                        floorPanels.get(i).setHighlightArrow(FloorPanel.ArrowType.DOWN, true);
//                    } else {
//                        if (i != 0) floorPanels.get(i).setHighlightArrow(FloorPanel.ArrowType.DOWN, false);
//                    }
                }

            }
        }.start();
    }

    void initCanvas() {
        double width = getWidth();
        double height = getHeight();
        canvas = new Canvas();
        canvas.setHeight(height);
        canvas.setWidth(width);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(0, titleHeight, width, titleHeight);
        for (int i = 0; i < building.getFloorCount(); ++i) {
            gc.strokeLine(0, titleHeight + i*floorHeight, width, titleHeight + i*floorHeight);
        }
//        for (int i = 0; i < model.getElevatorCount() + 1; ++i) {
//            gc.strokeLine(floorNumberWidth + i * floorWidth, 0, floorNumberWidth + i * floorWidth, height);
//        }
        getChildren().add(canvas);
    }




}
