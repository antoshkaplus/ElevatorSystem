package com.antoshkaplus.model;

import java.util.Random;

/**
 * Created by antoshkaplus on 10/23/14.
 */
public class RequestMaker extends Thread {

    private ElevatorSystem elevatorSystem;
    private Random random = new Random();

    @Override
    public void run() {
        while (true) {
            // mean wait value 3 sec
            int time = random.nextInt(10000);
            try {
                synchronized (this) {
                    wait(time);
                }
            } catch (InterruptedException e) {
            }
            if (elevatorSystem == null) continue;
            int floorCount = elevatorSystem.getFloorCount();
            int floor = random.nextInt(floorCount);
            Direction direction = random.nextBoolean() ? Direction.UP : Direction.DOWN;
            if (floor == 0) direction = Direction.UP;
            else if (floor == floorCount-1) direction = Direction.DOWN;
            elevatorSystem.addRequest(direction, floor);
        }
    }

    public void setElevatorSystem(ElevatorSystem elevatorSystem) {
        this.elevatorSystem = elevatorSystem;
    }
}
