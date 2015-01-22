package com.antoshkaplus.model;

import java.util.ArrayList;

/**
 * Created by antoshkaplus on 10/17/14.
 */
public class Building {
    private ArrayList<Elevator> elevators = new ArrayList<Elevator>();
    private int floorCount;
    private ElevatorSystem elevatorSystem;

    public Building(int floorCount, int elevatorCount) {
        this.floorCount = floorCount;
        for (int i = 0; i < elevatorCount; ++i) {
            Elevator elevator = new Elevator();
            elevator.start();
            elevators.add(elevator);
        }
        elevatorSystem = new ElevatorSystem(floorCount);
        for (Elevator elevator : getElevators()) {
            elevatorSystem.addElevator(elevator);
        }
    }

    public Iterable<Elevator> getElevators() {
        return elevators;
    }

    public Elevator getElevator(int id)  {
        for (Elevator e : elevators) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public boolean isElevatorFunctional(Elevator elevator) {
        return true;
    }

    public Direction getElevatorDirection(Elevator elevator) {
        return elevatorSystem.
    }

    public void onElevatorRequest(ElevatorRequest request) {
        elevatorSystem.addRequest(request);
    }

    public boolean isElevatorRequested(ElevatorRequest request) {
        return elevatorSystem.hasRequest(request);
    }

    public int getElevatorCount() {
        return elevators.size();
    }

    public int getFloorCount() {
        return floorCount;
    }
}
