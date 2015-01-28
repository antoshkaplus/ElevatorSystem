package com.antoshkaplus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by antoshkaplus on 1/22/15.
 *
 * bad thing is that we need to re-implement some parts from controls,
 * but not that much actually, and we don't need to mess up with controls events anymore
 *
 */
public class BuildingElevator extends Elevator {

    private ElevatorControls controls = new ElevatorControls();
    private OnButtonPressListener listener;
    private List<ControlsListenerAdapter> controlsListeners = new ArrayList<>();

    void PressButton(int floor) {
        if (listener != null) listener.onButtonPress(this, floor);
    }

    // for controller only
    public void setOnButtonPressListener(OnButtonPressListener listener) {
        this.listener = listener;
    }

    public ElevatorControls getControls() {
        return controls;
    }

    public void addControlsListener(ControlsListener listener) {
        controlsListeners.add(new ControlsListenerAdapter(listener));
    }

    public void removeControlsListener(ControlsListener listener) {
        for (ControlsListenerAdapter a : controlsListeners) {
            if (a.getListener() == listener) {
                a.unbind();
                controlsListeners.remove(a);
                return;
            }
        }
    }

    public interface OnButtonPressListener {
        void onButtonPress(BuildingElevator elevator, int floor);
    }

    public interface ControlsListener {
        void onDirectionChange(BuildingElevator elevator);
        void onHighlightButtonChange(BuildingElevator elevator, int floor, boolean highlight);
    }

    private class ControlsListenerAdapter implements ElevatorControls.Listener {

        public ControlsListener listener;

        public ControlsListenerAdapter(ControlsListener listener) {
            this.listener = listener;
            controls.addListener(this);
        }

        public ControlsListener getListener() {
            return listener;
        }

        @Override
        public void onDirectionChange(ElevatorControls controls) {
            this.listener.onDirectionChange(BuildingElevator.this);
        }

        @Override
        public void onHighlightButtonChange(ElevatorControls controls, int floor) {
            this.listener.onHighlightButtonChange(BuildingElevator.this, floor, controls.isHighlightedButton(floor));
        }

        void unbind() {
            controls.removeListener(this);
        }
    }

}
