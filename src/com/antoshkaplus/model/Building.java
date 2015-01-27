package com.antoshkaplus.model;

import java.util.ArrayList;

/**
 * Created by antoshkaplus on 10/17/14.
 *
 * May have many ElevatorSystems and control which one gets request
 */
public class Building {
    private ArrayList<BuildingElevator> elevators = new ArrayList<BuildingElevator>();
    private int floorCount;
    private ElevatorSystem elevatorSystem;

    public Building(int floorCount, int elevatorCount) {
        this.floorCount = floorCount;
        for (int i = 0; i < elevatorCount; ++i) {
            BuildingElevator elevator = new BuildingElevator();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    elevator.run();
                }
            });
            thread.start();
            elevators.add(elevator);
        }
        elevatorSystem = new ElevatorSystem();
        for (BuildingElevator elevator : getElevators()) {
            elevatorSystem.addElevator(elevator);
        }
    }

    public Iterable<BuildingElevator> getElevators() {
        return elevators;
    }

    public void onRequest(ElevatorRequest request) {
        elevatorSystem.addRequest(request);
    }

    // should some method that needs listener like onRequest

    public void onRequestSatisfied(ElevatorRequest request) {
        // notify visualizer listener ???
        // cancel highlighted floor panels
    }

    public int getElevatorCount() {
        return elevators.size();
    }

    public int getFloorCount() {
        return floorCount;
    }
}
