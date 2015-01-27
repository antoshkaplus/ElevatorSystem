package com.antoshkaplus.model;

/**
 * Created by antoshkaplus on 1/20/15.
 *
 * Task is to make elevator Idle from any state
 */
public class ElevatorController implements Elevator.Listener {
    private Listener listener = null;
    protected BuildingElevator elevator;

    public ElevatorController(BuildingElevator elevator) {
        this.elevator = elevator;
        this.elevator.addListener(this);
    }

    @Override
    public void onStateStart(Elevator elevator, Elevator.State state) {}

    @Override
    public void onStateFinish(Elevator elevator, Elevator.State state) {}

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public BuildingElevator getElevator() {
        return elevator;
    }

    protected void notifyOnFinish() {
        this.elevator.removeListener(this);
        if (listener != null) listener.onFinish(this);
    }

    public interface Listener {
        void onFinish(ElevatorController controller);
    }
}
