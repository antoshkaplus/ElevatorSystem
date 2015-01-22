package com.antoshkaplus.model;

/**
 * Created by antoshkaplus on 10/26/14.
 */
public class TargetElevatorController extends ElevatorController {

    public TargetElevatorController(Elevator elevator, int destinationFloor) {
        super(elevator);
        elevator.setDestination(destinationFloor);
    }

    @Override
    public void onStateFinish(Elevator elevator, Elevator.State state) {
        if (state == Elevator.State.MOVING_TO_DESTINATION) {
            elevator.setState(Elevator.State.OPENING_DOORS);
        } else if (state == Elevator.State.OPENING_DOORS) {
            if (getListener() != null) {
                elevator.removeListener(this);
                getListener().onFinish(this);
            }
        }
    }
}
