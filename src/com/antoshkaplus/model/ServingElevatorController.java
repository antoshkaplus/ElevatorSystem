package com.antoshkaplus.model;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by antoshkaplus on 10/16/14.
 */
public class ServingElevatorController extends ElevatorController implements BuildingElevator.OnButtonPressListener {
    // sorting depends on direction
    private SortedSet<Integer> targets;


    public ServingElevatorController(BuildingElevator elevator, Direction direction) {
        super(elevator);
        elevator.setOnButtonPressListener(this);
        elevator.getControls().setDirection(direction);
        targets = new TreeSet<Integer>(Direction.UP == direction ?
                                        Comparator.naturalOrder() :
                                        Comparator.reverseOrder());
    }

    public BuildingElevator getElevator() {
        return elevator;
    }

    public boolean addRequest(int floor) {
        if (!canStop(floor)) return false;
        addTarget(floor);
        return true;
    }

    @Override
    public void onButtonPress(BuildingElevator elevator, int floor) {
        if (!canStop(floor)) return;
        elevator.getControls().setHighlightButton(floor, true);
        addTarget(floor);
    }

    @Override
    public void onStateFinish(Elevator sender, Elevator.State state) {
        // going to use
        switch (state) {
            case OPENING_DOORS:
                elevator.setState(Elevator.State.PASSENGER_TRANSFER);
                break;
            case CLOSING_DOORS:
                if (targets.isEmpty()) {
                    notifyOnFinish();
                } else {
                    elevator.setDestination(targets.first());
                    elevator.setState(Elevator.State.MOVING_TO_DESTINATION);
                }
                break;
            case MOVING_TO_DESTINATION:
                elevator.setState(Elevator.State.OPENING_DOORS);
                targets.remove(elevator.getFloor());
                elevator.getControls().setHighlightButton(elevator.getFloor(), false);
                break;
            case PASSENGER_TRANSFER:
                elevator.setState(Elevator.State.CLOSING_DOORS);
                break;
        }
    }

    private boolean canStop(int floor) {
        return isPassingBy(floor) && elevator.getArrivingTime(floor) > 0;
    }

    private boolean isPassingBy(int floor) {
        Direction dir = elevator.getControls().getDirection();
        return (dir == Direction.DOWN && floor <= elevator.getFloorLocation()) ||
                (dir == Direction.UP && floor >= elevator.getFloorLocation());
    }

    private void addTarget(int floor) {
        targets.add(floor);
        if (elevator.getDestination() != targets.first()) elevator.setDestination(targets.first());
    }

    public Iterable<Integer> getTargets() {
        return targets;
    }

    @Override
    protected void notifyOnFinish() {
        super.notifyOnFinish();
        elevator.setOnButtonPressListener(null);
    }
}
