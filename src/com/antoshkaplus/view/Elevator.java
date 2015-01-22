package com.antoshkaplus.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by antoshkaplus on 10/23/14.
 */
class Elevator extends Group {
    private Rectangle frameBorder;
    private Rectangle frameBody;
    private Door leftDoor;
    private Door rightDoor;

    // need this value in case of surprise relocation or resize
    private double openPortion = 0;
    // need to store this value to know open indent and
    // for using method updateDoorLocations
    private double doorWidth;

    private static final double FRAME_BORDER_WIDTH = 1;
    private static final double FRAME_BODY_WIDTH = 7;
    private static final double DOOR_PADDING = 2;


    public Elevator() {
        frameBorder = new Rectangle();
        frameBorder.setFill(null);
        frameBorder.setStroke(Color.BLACK);
        // will draw both with big fat line
        frameBorder.setStrokeWidth(2*FRAME_BORDER_WIDTH + FRAME_BODY_WIDTH);
        frameBody = new Rectangle();
        frameBody.setFill(null);
        frameBody.setStroke(Color.WHITE);
        frameBody.setStrokeWidth(FRAME_BODY_WIDTH);
        leftDoor = new Door();
        rightDoor = new Door();

        getChildren().add(frameBorder);
        getChildren().add(frameBody);
        getChildren().add(leftDoor);
        getChildren().add(rightDoor);
    }

    private void updateDoorLocations() {
        // don't forget about open portion
//        leftDoor.relocate(
//                frameBody.getX() + FRAME_BODY_WIDTH/2 + FRAME_BORDER_WIDTH + DOOR_PADDING - openPortion*doorWidth,
//                frameBody.getY() + FRAME_BODY_WIDTH/2 + FRAME_BORDER_WIDTH + DOOR_PADDING);
//
        leftDoor.setTranslateX(/*frameBody.getX()*/ + FRAME_BODY_WIDTH/2 + FRAME_BORDER_WIDTH + DOOR_PADDING - openPortion*doorWidth);
        leftDoor.setTranslateY(/*frameBody.getY()*/ + FRAME_BODY_WIDTH/2 + FRAME_BORDER_WIDTH + DOOR_PADDING);

        rightDoor.relocate(
                frameBody.getX() + FRAME_BODY_WIDTH/2 + FRAME_BORDER_WIDTH + 2*DOOR_PADDING + doorWidth + openPortion*doorWidth,
                frameBody.getY() + FRAME_BODY_WIDTH/2 + FRAME_BORDER_WIDTH + DOOR_PADDING);
    }


    void setOpenPortion(double openPortion) {
        // just update doors locations
        this.openPortion = openPortion;
        updateDoorLocations();
    }

    @Override
    public void relocate(double x, double y) {
        frameBorder.setX(x + FRAME_BORDER_WIDTH + FRAME_BODY_WIDTH/2);
        frameBorder.setY(y + FRAME_BORDER_WIDTH + FRAME_BODY_WIDTH/2);
        frameBody.setX(x + FRAME_BORDER_WIDTH + FRAME_BODY_WIDTH/2);
        frameBody.setY(y + FRAME_BORDER_WIDTH + FRAME_BODY_WIDTH/2);
        updateDoorLocations();
    }

    @Override
    public void resize(double width, double height) {
        frameBody.setWidth(width - (2*FRAME_BORDER_WIDTH + FRAME_BODY_WIDTH));
        frameBody.setHeight(height - (2*FRAME_BORDER_WIDTH + FRAME_BODY_WIDTH));
        frameBorder.setWidth(width - 2*FRAME_BORDER_WIDTH - FRAME_BODY_WIDTH);
        frameBorder.setHeight(height - 2*FRAME_BORDER_WIDTH - FRAME_BODY_WIDTH);

        double doorWidth = (width - (2*(FRAME_BODY_WIDTH + 2*FRAME_BORDER_WIDTH) +DOOR_PADDING +3*DOOR_PADDING))/2.;
        double doorHeight = (height - (2*(FRAME_BODY_WIDTH + 2*FRAME_BORDER_WIDTH + 2*DOOR_PADDING)));
        this.doorWidth = doorWidth;
        leftDoor.resize(doorWidth, doorHeight);
        rightDoor.resize(doorWidth, doorHeight);
        updateDoorLocations();
    }

    void setColor(Color color) {
        frameBody.setStroke(color);
        leftDoor.setColor(color);
        rightDoor.setColor(color);
    }

    private class Door extends Group {
        private Rectangle body;
        private Rectangle border;

        private static final double BORDER_WIDTH = 1;

        public Door() {
            body = new Rectangle();
            body.setStroke(null);
            // can make default constants here
            body.setFill(Color.WHITE);
            border = new Rectangle();
            border.setStroke(Color.BLACK);
            border.setStrokeWidth(BORDER_WIDTH);
            border.setFill(null);
            getChildren().add(body);
            getChildren().add(border);
        }

        @Override
        public void resize(double width, double height) {
            System.out.println(width + " " + height);

            border.setWidth(width);
            border.setHeight(height);
            body.setWidth(width);
            body.setHeight(height);
        }

        @Override
        public void relocate(double x, double y) {
            border.relocate(x, y);
            body.relocate(x, y);
        }

        public void setColor(Color color) {
            body.setFill(color);
        }
    }


}