package com.antoshkaplus.model;

import java.util.*;

/**
 * Created by antoshkaplus on 10/16/14.
 */
public class ElevatorSystem implements  ElevatorController.Listener {
    // here we have three states for the elevator
    // idle => target => serving => idle

    private EnumMap<Direction, List<Integer>> requests;

    private List<ServingElevatorController> servingElevatorControllers = Collections.synchronizedList(new ArrayList<ServingElevatorController>());
    private List<TargetElevatorController> targetElevatorControllers = Collections.synchronizedList(new ArrayList<TargetElevatorController>());
    private List<Direction> targetDirection = new ArrayList<Direction>();
    private List<Elevator> idleElevators = Collections.synchronizedList(new ArrayList<Elevator>());


    public ElevatorSystem() {
        requests = new EnumMap<Direction, List<Integer>>(Direction.class);
        requests.put(Direction.DOWN, Collections.synchronizedList(new ArrayList<Integer>()));
        requests.put(Direction.UP, Collections.synchronizedList(new ArrayList<Integer>()));
    }

    @Override
    public void onFinish(ElevatorController controller) {
        if (controller instanceof TargetElevatorController) {
            targetElevatorControllers.remove(controller);
            servingElevatorControllers.add(new ServingElevatorController(controller.getElevator(), targetDirection.get(0)));
        } else if (controller instanceof ServingElevatorController) {
            servingElevatorControllers.remove(controller);
            idleElevators.add(controller.getElevator());
        }
    }

    public void addElevator(Elevator elevator) {
        idleElevators.add(elevator);
    }

    public void addRequest(ElevatorRequest request) {
        Direction direction = request.direction;
        int floor = request.floor;
        if (requests.get(direction).contains(floor)) {
            return;
        }
        for (ServingElevatorController c : servingElevatorControllers) {
            if (c.getDirection() == direction && c.addTarget(floor)) {
                return;
            }
        }
        if (idleElevators.isEmpty()) {
            requests.get(direction).add(floor);
            return;
        }
        int i = idleElevators.size() - 1;
        Elevator el = idleElevators.get(i);
        idleElevators.remove(i);
        targetElevatorControllers.add(new TargetElevatorController(el, floor));
        targetDirection.add(direction);
    }
}
