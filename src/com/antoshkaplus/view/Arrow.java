package com.antoshkaplus.view;

import javafx.geometry.Orientation;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Created by antoshkaplus on 1/26/15.
 */
public class Arrow extends Polygon {

    public Arrow(double width, double height, Type type) {
        // UP arrow
        super(width/2, 0, width, height, 0, height);
        if (type == Type.DOWN) setRotate(180);
        setStroke(Color.BLACK);
        setFill(Color.BURLYWOOD);
    }

    public enum Type {
        UP,
        DOWN
    }
}
