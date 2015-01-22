package com.antoshkaplus.view;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Created by antoshkaplus on 10/23/14.
 */

class FloorPanel extends Group {
    Rectangle panel;
    Arrow upArrow;
    Arrow downArrow;

    ArrowType panelType = ArrowType.BOTH;

    Color arrowHighlightColor;
    Color arrowCasualColor;

    private static final double MIN_SPACE = 15;

    FloorPanel(ArrowType type) {
        super();
        this.panelType = type;
        panel = new Rectangle();
        panel.setStroke(Color.BLACK);
        panel.setStrokeWidth(1);
        panel.setFill(null);
        getChildren().add(panel);
        if (type == ArrowType.UP || type == ArrowType.BOTH) {
            upArrow = new Arrow();
            getChildren().add(upArrow);
        }
        if (type == ArrowType.DOWN || type == ArrowType.BOTH) {
            downArrow = new Arrow();
            getChildren().add(downArrow);
        }
    }

    private void updateArrowPoints() {
        double x = panel.getX();
        double y = panel.getY();
        double w = panel.getWidth();
        double h = panel.getHeight();

        System.out.println(x + " " + y + " " + w +" " + h);

        double w_0 = w - 2*MIN_SPACE;
        double h_0 = Math.sqrt(3.)/2. * w_0;
        ObservableList<Double> ps = null;
        double w_half = w/2;
        double w_0_half = w_0/2.;
        if (panelType == ArrowType.UP) {
            ps = upArrow.getPoints();
            double y_0 = y + (h - h_0)/2.;
            ps.setAll(
                    x + w_half, y_0,
                    x + w_half - w_0_half, y_0 + h_0,
                    x + w_half + w_0_half, y_0 + h_0);
        } else if (panelType == ArrowType.DOWN) {
            ps = downArrow.getPoints();
            double y_0 = y + (h - h_0)/2.;
            ps.setAll(
                    x + w_half, y_0 + h_0,
                    x + w_half - w_0_half, y_0,
                    x + w_half + w_0_half, y_0);
        } else { // panelType == BOTH
            double y_0 = y + (h - 2*h_0 - MIN_SPACE)/2.;
            ps = upArrow.getPoints();
            ps.setAll(
                    x + w_half, y_0,
                    x + w_half - w_0_half, y_0 + h_0,
                    x + w_half + w_0_half, y_0 + h_0);
            ps = downArrow.getPoints();
            ps.setAll(
                    x + w_half, y_0 + h_0 + MIN_SPACE + h_0,
                    x + w_half - w_0_half, y_0 + h_0 + MIN_SPACE,
                    x + w_half + w_0_half, y_0 + h_0 + MIN_SPACE);
        }
    }

    @Override
    public void resize(double width, double height) {
        panel.setWidth(width);
        panel.setHeight(height);
        updateArrowPoints();
    }

    @Override
    public void relocate(double x, double y) {
        panel.setX(x);
        panel.setY(y);
        updateArrowPoints();
    }

    public void setArrowCasualColor(Color color) {
        arrowCasualColor = color;
        if (panelType != ArrowType.DOWN) upArrow.casualColorUpdated();
        if (panelType != ArrowType.UP) downArrow.casualColorUpdated();
    }

    public void setArrowHighlightColor(Color color) {
        arrowHighlightColor = color;
        if (panelType != ArrowType.DOWN) upArrow.highlightColorUpdated();
        if (panelType != ArrowType.UP) downArrow.highlightColorUpdated();
    }

    // will change color of arrow
    public void setHighlightArrow(ArrowType type, boolean highlighted) {
        if (type == ArrowType.BOTH || type == ArrowType.UP) upArrow.setHighlighted(highlighted);
        if (type == ArrowType.BOTH || type == ArrowType.DOWN) downArrow.setHighlighted(highlighted);
    }

    enum ArrowType {
        UP,
        DOWN,
        BOTH
    }

    class Arrow extends Polygon {
        Arrow() {
            super();
            setStroke(Color.BLACK);
        }

        boolean highlighted = false;

        public boolean isHighlighted() {
            return highlighted;
        }

        public void setHighlighted(boolean highlighted) {
            this.highlighted = highlighted;
            setFill(highlighted ? arrowHighlightColor : arrowCasualColor);
        }

        void casualColorUpdated() {
            if (!highlighted) setFill(arrowCasualColor);
        }

        void highlightColorUpdated() {
            if (highlighted) setFill(arrowHighlightColor);
        }
    }

}