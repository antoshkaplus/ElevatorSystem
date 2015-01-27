package com.antoshkaplus.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by antoshkaplus on 1/22/15.
 *
 * This class is about visualization of elevator
 */
public class ElevatorControls {

    private List<Listener> listeners = new ArrayList<Listener>();
    private Direction direction = Direction.NONE;
    private SortedSet<Integer> highlightedButtons = new TreeSet<Integer>();
    private boolean functional = false;

    void setDirection(Direction direction) {
        this.direction = direction;
        listeners.forEach((lis) -> lis.onDirectionChange(this));
    }

    public Direction getDirection() {
        return direction;
    }

    public void setHighlightButton(int floor, boolean highlighted) {
        if (highlighted) highlightedButtons.add(floor);
        else highlightedButtons.remove(floor);
        listeners.forEach((lis) -> lis.onHighlightButtonChange(this, floor));
    }

    public boolean isHighlightedButton(int floor) {
        return highlightedButtons.contains(floor);
    }

    public SortedSet<Integer> getHighlightedButtons() {
        return highlightedButtons;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    public boolean isFunctional() {
        return functional;
    }

    public interface Listener {
        void onDirectionChange(ElevatorControls controls);
        void onHighlightButtonChange(ElevatorControls controls, int floor);
    }

}
