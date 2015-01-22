package com.antoshkaplus.model;

/**
 * Created by antoshkaplus on 1/20/15.
 */
public class ElevatorController implements Elevator.Listener {
    private Listener listener = null;
    private Elevator elevator;

    public ElevatorController(Elevator elevator) {
        this.elevator = elevator;
        this.elevator.addListener(this);
    }

    @Override
    public void onStateStart(Elevator elevator, Elevator.State state) {}

    @Override
    public void onStateFinish(Elevator elevator, Elevator.State state) {}

    @Override
    public void onButtonPress(Elevator elevator, int floor) {}


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public interface Listener {
        void onFinish(ElevatorController controller);
    }
}
