package com.antoshkaplus.model;

/**
 * Created by antoshkaplus on 10/26/14.
 */
public class TargetElevatorController extends ElevatorController {

    private ElevatorRequest request;

    public TargetElevatorController(BuildingElevator elevator, ElevatorRequest request) {
        super(elevator);
        this.request = request;
        elevator.setDestination(request.floor);
        // or maybe some other
        elevator.setState(Elevator.State.MOVING_TO_DESTINATION);
    }

    @Override
    public void onStateFinish(Elevator elevator, Elevator.State state) {
        if (state == Elevator.State.MOVING_TO_DESTINATION) {
            elevator.setState(Elevator.State.OPENING_DOORS);
        } else if (state == Elevator.State.OPENING_DOORS) {
            notifyOnFinish();
        }
    }

    ElevatorRequest getRequest() {
        return request;
    }
}
