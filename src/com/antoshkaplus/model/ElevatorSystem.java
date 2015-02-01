package com.antoshkaplus.model;

import com.sun.tools.javac.util.*;

import java.util.*;
import java.util.List;

/**
 * Created by antoshkaplus on 10/16/14.
 */
public class ElevatorSystem implements  ElevatorController.Listener {
    // here we have three states for the elevator
    // idle => target => serving => idle

    private Random random = new Random();
    private final EnumMap<Direction, List<Integer>> requests = new EnumMap<>(Direction.class);

    private final List<ServingElevatorController> servingElevatorControllers = Collections.synchronizedList(new ArrayList<>());
    private final List<TargetElevatorController> targetElevatorControllers = Collections.synchronizedList(new ArrayList<>());
    private final List<BuildingElevator> idleElevators = Collections.synchronizedList(new ArrayList<>());

//    private List<Listener> listeners = new ArrayList<>();

    public ElevatorSystem() {
        requests.put(Direction.DOWN, Collections.synchronizedList(new ArrayList<>()));
        requests.put(Direction.UP, Collections.synchronizedList(new ArrayList<>()));
    }

    @Override
    public void onFinish(ElevatorController controller) {
        if (controller instanceof TargetElevatorController) {
            // something is not right here
            TargetElevatorController tcl = (TargetElevatorController)controller;
            targetElevatorControllers.remove(controller);
            Direction dir = tcl.getRequest().direction;
            ServingElevatorController c = new ServingElevatorController(controller.getElevator(), dir);
            // i can lock just direction
            synchronized (requests) {
                List<Integer> rs = requests.get(dir);
                rs.removeIf(c::addRequest);
            }
            c.setListener(this);
            servingElevatorControllers.add(c);
        } else if (controller instanceof ServingElevatorController) {
            servingElevatorControllers.remove(controller);
            Direction dir;
            int floor;
            synchronized (requests) {
                dir = requests.get(Direction.DOWN).size() > requests.get(Direction.UP).size() ?
                        Direction.DOWN : Direction.UP;
                List<Integer> rs = requests.get(dir);
                if (rs.isEmpty()) {
                    idleElevators.add(controller.getElevator());
                    return;
                }
                // if dir = up find lowest floor
                // else take highest
                int removeIndex = 0;
                if (dir == Direction.UP) {
                    for (int i = 0; i < rs.size(); ++i) {
                        if (rs.get(i) < rs.get(removeIndex)) removeIndex = i;
                    }
                } else {
                    for (int i = 1; i < rs.size(); ++i) {
                        if (rs.get(i) > rs.get(removeIndex)) removeIndex = i;
                    }
                }
                floor = rs.remove(removeIndex);
            }
            assignRequest(controller.getElevator(), new ElevatorRequest(floor, dir));
        }
    }

    public void addElevator(BuildingElevator elevator) {
        idleElevators.add(elevator);
    }

    public void addRequest(ElevatorRequest request) {
        Direction direction = request.direction;
        int floor = request.floor;
        synchronized (requests) {
            if (requests.get(direction).contains(floor)) {
                return;
            }
        }
        for (ServingElevatorController c : servingElevatorControllers) {
            if (c.getElevator().getControls().getDirection() == request.direction && c.addRequest(floor)) {
                // request added to controller to handle
                return;
            }
        }
        BuildingElevator elevator;
        synchronized (idleElevators) {
            if (idleElevators.isEmpty()) {
                synchronized (requests) {
                    requests.get(direction).add(floor);
                }
                return;
            }
            elevator = idleElevators.remove(idleElevators.size() - 1);
        }
        assignRequest(elevator, request);
    }

    private void assignRequest(BuildingElevator elevator, ElevatorRequest request) {
        TargetElevatorController controller = new TargetElevatorController(elevator, request);
        controller.setListener(this);
        targetElevatorControllers.add(controller);
    }

    public Iterable<Integer> getElevatorStops(BuildingElevator bel) {
        // sometimes may not find elevator due to rearrangements
        while (true) {
            if (idleElevators.contains(bel)) {
                return new ArrayList<>();
            }
            for (TargetElevatorController c : targetElevatorControllers) {
                if (c.elevator == bel) {
                    List a = new ArrayList<>();
                    a.add(c.getRequest().floor);
                    return a;
                }
            }
            for (ServingElevatorController c : servingElevatorControllers) {
                if (c.elevator == bel) {
                    return c.getTargets();
                }
            }
        }
    }
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
