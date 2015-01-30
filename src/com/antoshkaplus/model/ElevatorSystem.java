package com.antoshkaplus.model;

import java.util.*;

/**
 * Created by antoshkaplus on 10/16/14.
 */
public class ElevatorSystem implements  ElevatorController.Listener {
    // here we have three states for the elevator
    // idle => target => serving => idle

    private Random random = new Random();
    private EnumMap<Direction, List<Integer>> requests;

    private List<ServingElevatorController> servingElevatorControllers = Collections.synchronizedList(new ArrayList<>());
    private List<TargetElevatorController> targetElevatorControllers = Collections.synchronizedList(new ArrayList<>());
    private volatile List<BuildingElevator> idleElevators = Collections.synchronizedList(new ArrayList<>());

    private Object idleLock = new Object();

//    private List<Listener> listeners = new ArrayList<>();

    public ElevatorSystem() {
        requests = new EnumMap<Direction, List<Integer>>(Direction.class);
        requests.put(Direction.DOWN, Collections.synchronizedList(new ArrayList<Integer>()));
        requests.put(Direction.UP, Collections.synchronizedList(new ArrayList<Integer>()));
    }

    @Override
    public void onFinish(ElevatorController controller) {
        if (controller instanceof TargetElevatorController) {
            // something is not right here
            TargetElevatorController tcl = (TargetElevatorController)controller;
            targetElevatorControllers.remove(controller);
            Direction dir = tcl.getRequest().direction;
            ServingElevatorController c = new ServingElevatorController(controller.getElevator(), dir);
            List<Integer> rs = requests.get(dir);
            for (Integer r : rs) {
                if (c.addRequest(r)) {
                    rs.remove(r);
                }
            }
            c.setListener(this);
            servingElevatorControllers.add(c);
        } else if (controller instanceof ServingElevatorController) {
            servingElevatorControllers.remove(controller);
            synchronized (idleLock) {
                idleElevators.add(controller.getElevator());
                Direction dir = requests.get(Direction.DOWN).size() > requests.get(Direction.UP).size() ?
                        Direction.DOWN : Direction.UP;
                List<Integer> rs = requests.get(dir);
                if (rs.isEmpty()) return;
                int floor = rs.remove(rs.size() - 1);
                assignToIdle(new ElevatorRequest(floor, dir));
            }
        }
    }

    public void addElevator(BuildingElevator elevator) {
        idleElevators.add(elevator);
    }

    public void addRequest(ElevatorRequest request) {
        Direction direction = request.direction;
        int floor = request.floor;
        if (requests.get(direction).contains(floor)) {
            return;
        }
        for (ServingElevatorController c : servingElevatorControllers) {
            if (c.getElevator().getControls().getDirection() == request.direction && c.addRequest(floor)) {
                // request added to controller to handle
                return;
            }
        }
        synchronized (idleLock) {
            if (idleElevators.isEmpty()) {
                requests.get(direction).add(floor);
                return;
            }
            assignToIdle(request);
        }
    }

    private void assignToIdle(ElevatorRequest request) {
        int i = idleElevators.size() - 1;
        BuildingElevator el = idleElevators.get(i);
        idleElevators.remove(i);
        TargetElevatorController controller = new TargetElevatorController(el, request);
        controller.setListener(this);
        targetElevatorControllers.add(controller);
    }



//
//    public void addStateListener(Listener listener) {
//        listeners.add(listener);
//    }
//
//    public void removeStateListener(Listener listener) {
//        listeners.remove(listener);
//    }
//
//    private void notifyListeners(ElevatorRequest request) {
//        listeners.forEach(lis -> lis.onRequestSatisfied(request));
//    }
//
//
//    public interface Listener {
//        void onRequestSatisfied(ElevatorRequest request);
//    }
}
