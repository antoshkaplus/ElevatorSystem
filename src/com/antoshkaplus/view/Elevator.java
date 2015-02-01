package com.antoshkaplus.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Created by antoshkaplus on 10/23/14.
 */
class Elevator extends Parent {
    private Rectangle frame = new Rectangle();
    private Rectangle leftDoor = new Rectangle();
    private Rectangle rightDoor = new Rectangle();

    // need this value in case of surprise relocation or resize
    private double openDoorsPortion = 0.2;
    private double floor = 1;

    public Elevator() {
        super();
        frame.setFill(Color.WHITE);
        leftDoor.setFill(Color.YELLOW);
        rightDoor.setFill(Color.YELLOW);

        frame.setStroke(Color.BLACK);
        leftDoor.setStroke(Color.CYAN);
        rightDoor.setStroke(Color.CYAN);

        getChildren().add(frame);
        getChildren().add(leftDoor);
        getChildren().add(rightDoor);
    }

    public void setOpenDoorsPortion(double openPortion) {
        // just update doors locations
        this.openDoorsPortion = openPortion;
        updateLocation();
    }

    // updating location by looking at parent
    public void setFloor(double floor) {
        this.floor = floor;
        updateLocation();

    }

    // will be called when parent is going to resize itself
    @Override
    public void relocate(double x, double y) {
        updateSize();
        updateLocation();
    }

    @Override
    protected double computePrefHeight(double width) {
        return 0;
    }

    @Override
    protected double computePrefWidth(double height) {
        return 0;
    }

    public void updateSize() {
        Shaft sh = (Shaft)getParent();
        int f = sh.getFloorCount();
        double w = sh.getWidth();
        // height per floor
        double h = sh.getHeight()/f;
        // 4 because we will actually open the door
        double unit = 0.8 * Math.min(h, w/2);

        frame.setWidth(unit);
        frame.setHeight(unit);

        leftDoor.setWidth(unit/2);
        leftDoor.setHeight(unit);

        rightDoor.setWidth(unit/2);
        rightDoor.setHeight(unit);
    }

    private void updateLocation() {
        Shaft sh = (Shaft)getParent();
        double w = sh.getWidth();
        double h = sh.getHeight();
        double perFloor = h/sh.getFloorCount();
        double y = (sh.getFloorCount() - floor) * perFloor - perFloor + (perFloor - frame.getHeight())/2;
        double m = w/2;

        frame.setLayoutX((w - frame.getWidth()) / 2);
        leftDoor.setLayoutX(m - leftDoor.getWidth() - openDoorsPortion * leftDoor.getWidth());
        rightDoor.setLayoutX(m + openDoorsPortion * rightDoor.getWidth());

        setLayoutY(y);
    }
}