package com.antoshkaplus.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


// need to create multiple events

/**
 * Created by antoshkaplus on 10/16/14.
 */
// will assume that elevator moves with constant speed
public class Elevator {
    private static AtomicInteger lastId = new AtomicInteger(0);
    private final int id = lastId.getAndIncrement();

    private int destinationFloor;

    private List<StateListener> listeners = new ArrayList<StateListener>();
    // variables change only inside this thread class
    private double currentFloorLocation = 0;
    private double doorOpenPortion = 0;
    // how many floors per second
    private final double elevatorSpeed = 0.2;
    // open portion per second
    private final double doorSpeed = 0.2;
    // will be used as getter only
    private EnumMap<State, StateInterface> states = new EnumMap<State, StateInterface>(State.class);
    private State currentState = State.IDLE;


    public Elevator() {
        states.put(State.OPENING_DOORS, new OpeningDoors());
        states.put(State.CLOSING_DOORS, new ClosingDoors());
        states.put(State.IDLE, new Idle());
        states.put(State.MOVING_TO_DESTINATION, new MovingToDestination());
        states.put(State.PASSENGER_TRANSFER, new PassengerTransfer());
        setState(State.IDLE);
    }

    public void addStateListener(StateListener listener) {
        listeners.add(listener);
    }
    public void removeStateListener(StateListener listener) {
        listeners.remove(listener);
    }

    public int getElevatorId() {
        return id;
    }

    public void run() {
        while (true) {
            states.get(currentState).run();
        }
    }

    public double getFloorLocation() {
        return currentFloorLocation;
    }
    public int getFloor() {
        return (int)Math.round(currentFloorLocation);
    }

    public double getDoorOpenPortion() {
        return doorOpenPortion;
    }
    // returns milliseconds
    public long getArrivingTime(int floor) {
        return 0;
    }

    // should be called after elevator calls controller.onIdle()
    // or can be called while moving to particular target
    public synchronized void setDestination(int floor) {
        destinationFloor = floor;
    }

    public int getDestination() {
        return destinationFloor;
    }

    public synchronized void setState(State state) {
        currentState = state;
        states.get(state).init();
    }

    public boolean isFirstCloser(int floor_0, int floor_1) {
        return Math.abs(currentFloorLocation - floor_0) <
                Math.abs(currentFloorLocation - floor_1);
    }

    private interface StateInterface {
        public void init();
        public void run();
    }

    private class Idle implements  StateInterface {
        @Override
        public void init() {
            notifyOnStateStart(State.IDLE);
        }
        @Override
        public void run() {}
    }

    private class PassengerTransfer implements StateInterface {
        private long startPassengerTransferTime;
        // seconds used
        private static final int TRANSFER_TIME = 4;

        @Override
        public void init() {
            notifyOnStateStart(State.PASSENGER_TRANSFER);
            startPassengerTransferTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            double transferTime = (System.currentTimeMillis() - startPassengerTransferTime) * 0.001;
            if (transferTime > TRANSFER_TIME) {
                notifyOnStateFinish(State.PASSENGER_TRANSFER);
            }
        }
    }

    // state
    private class OpeningDoors implements StateInterface {
        private long startOpenDoorsTime;

        @Override
        public void init() {
            startOpenDoorsTime = System.currentTimeMillis();
            notifyOnStateStart(State.OPENING_DOORS);
        }

        @Override
        public void run() {
            double indent = (System.currentTimeMillis() - startOpenDoorsTime) * 0.001 * doorSpeed;
            if (indent > 1) {
                doorOpenPortion = 1;
                notifyOnStateFinish(State.OPENING_DOORS);
            } else {
                doorOpenPortion = indent;
            }
        }
    }

    private class ClosingDoors implements StateInterface {
        private long startCloseDoorsTime;

        @Override
        public void init() {
            startCloseDoorsTime = System.currentTimeMillis();
            notifyOnStateStart(State.CLOSING_DOORS);
        }

        @Override
        public void run() {
            double nonIndent = (System.currentTimeMillis() - startCloseDoorsTime) * 0.001 * doorSpeed;
            if (nonIndent > 1) {
                doorOpenPortion = 0;
                notifyOnStateFinish(State.CLOSING_DOORS);
            } else {
                doorOpenPortion = 1 - nonIndent;
            }
        }
    }

    private class MovingToDestination implements StateInterface {
        private long startMoveToDestinationTime;
        private double startFloorLocation;
        // need local destination floor in case if user would like
        // to change it inside outer object
        private int destinationFloor;

        @Override
        public void init() {
            startMoveToDestinationTime = System.currentTimeMillis();
            startFloorLocation = currentFloorLocation;
            destinationFloor = Elevator.this.destinationFloor;
            notifyOnStateStart(State.MOVING_TO_DESTINATION);
        }

        @Override
        public void run() {
            //System.out.println("hi! " + currentFloorLocation);
            // this guy is actually signed
            double distance = (System.currentTimeMillis() - startMoveToDestinationTime) * 0.001 * elevatorSpeed;
            double vector = destinationFloor - startFloorLocation;
            if (Math.abs(distance) > Math.abs(vector)) {
                currentFloorLocation = destinationFloor;
                // just came to destination
                notifyOnStateFinish(State.MOVING_TO_DESTINATION);
            } else {
                currentFloorLocation = startFloorLocation + Math.signum(vector) * distance;
                //System.out.println("move " + currentFloorLocation);
            }
        }
    }

    private void notifyOnStateStart(State state) {
        for (StateListener listener : listeners) {
            listener.onStateStart(this, state);
        }
    }

    private void notifyOnStateFinish(State state) {
        for (StateListener listener : listeners) {
            listener.onStateFinish(this, state);
        }
    }

    public interface StateListener {
        public void onStateStart(Elevator elevator, State state);
        public void onStateFinish(Elevator elevator, State state);
    }

    /**
     * Created by antoshkaplus on 1/19/15.
     */
    public static enum State {
        OPENING_DOORS,
        CLOSING_DOORS,
        MOVING_TO_DESTINATION,
        PASSENGER_TRANSFER,
        IDLE
    }
}
