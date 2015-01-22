package com.antoshkaplus.model;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by antoshkaplus on 10/16/14.
 */
public class ServingElevatorController extends ElevatorController {
    private Elevator elevator;
    private Direction direction;
    private SortedSet<Integer> targets = new TreeSet<Integer>();

    public ServingElevatorController(Elevator elevator, Direction direction) {
        super(elevator);
        this.direction = direction;
    }

    @Override
    public void onButtonPress(Elevator elevator, int floor) {
        addTarget(floor);
    }

    public Direction getDirection() {
        return direction;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public boolean addTarget(int floor) {
        if (!isPassingBy(floor) || elevator.getArrivingTime(floor) < 0) {
            return false;
        }
        targets.add(floor);
        if (elevator.getDestination() != targets.first()) {
            elevator.setDestination(targets.first());
        }
        return true;
    }

    private boolean isPassingBy(int floor) {
        return (direction == Direction.DOWN && floor <= elevator.getFloorLocation()) ||
                (direction == Direction.UP && floor >= elevator.getFloorLocation());
    }

    @Override
    public void onStateFinish(Elevator elevator, Elevator.State state) {
        switch (state) {
            case OPENING_DOORS:
                elevator.setState(Elevator.State.PASSENGER_TRANSFER);
                break;
            case CLOSING_DOORS:
                if (targets.isEmpty() && getListener() != null) {
                    getListener().onFinish(this);
                } else {
                    elevator.setDestination(targets.first());
                    elevator.setState(Elevator.State.MOVING_TO_DESTINATION);
                }
                break;
            case MOVING_TO_DESTINATION:
                elevator.setState(Elevator.State.OPENING_DOORS);
                targets.remove(targets.first());
                break;
            case PASSENGER_TRANSFER:
                elevator.setState(Elevator.State.CLOSING_DOORS);
                break;
        }
    }
}
