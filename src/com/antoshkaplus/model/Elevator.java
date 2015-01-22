package com.antoshkaplus.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


// need to create multiple events

/**
 * Created by antoshkaplus on 10/16/14.
 */
// will assume that elevator moves with constant speed
public class Elevator extends Thread {
    private static AtomicInteger lastId = new AtomicInteger(0);

    private List<Listener> listeners = new ArrayList<Listener>();

    private SortedSet<Integer> highlightedButtons = Collections.synchronizedSortedSet(new TreeSet<Integer>());

    private final int id = lastId.getAndIncrement();

    // variables change only inside this thread class
    private double currentFloorLocation = 0;
    private double doorOpenPortion = 0;

    // how many floors per second
    private final double elevatorSpeed = 0.2;
    // open portion per second
    private final double doorSpeed = 0.2;

    // will be used as getter only
    private EnumMap<State, StateInterface> states = new EnumMap<State, StateInterface>(State.class);
    private AtomicReference<StateInterface> currentState = new AtomicReference<StateInterface>();

    public Elevator() {
        states.put(State.OPENING_DOORS, new OpeningDoors());
        states.put(State.CLOSING_DOORS, new ClosingDoors());
        states.put(State.IDLE, new Idle());
        // will take this state when was IDLE and
        states.put(State.MOVING_TO_DESTINATION, new MovingToDestination());
        states.put(State.PASSENGER_TRANSFER, new PassengerTransfer());

        setState(State.IDLE);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }


    public int getElevatorId() {
        return id;
    }

    @Override
    public void run() {
        while (true) {
            //if (currentState.get() instanceof MovingToDestination)System.out.println("hi! " + currentFloorLocation);
            currentState.get().run();
        }
    }

    SortedSet<Integer> getHighlightedButtons() {
        return highlightedButtons;
    }

    public void highlightButton(int floor) {
        highlightedButtons.add(floor);
    }

    public void cancelHighlightButton(int floor) {
        highlightedButtons.remove(floor);
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

        System.out.println("coming to " + floor);
        MovingToDestination s = (MovingToDestination)states.get(State.MOVING_TO_DESTINATION);

            s.setDestination(floor);
            setState(State.MOVING_TO_DESTINATION);

    }
    public int getDestination() {
        MovingToDestination s = (MovingToDestination)states.get(State.MOVING_TO_DESTINATION);
        return s.getDestination();
    }


    public synchronized void setState(State state) {
        StateInterface newState = states.get(state);
        if (newState == currentState) return;
        // can actually and state start state here
        notifyOnStateFinish(currentState);
        currentState = state;
        currentState.set(states.get(state));
        notifyOnStateStart(currentState);
        currentState.get().init();
    }

    public boolean isMovingToDestination() {
        return currentState.get() instanceof MovingToDestination;
    }

    public boolean isIdle() {
        return currentState.get() instanceof Idle;
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
        private int destinationFloor;

        @Override
        public void init() {
//            startMoveToDestinationTime = System.currentTimeMillis();
//            startFloorLocation = currentFloorLocation;
            notifyOnStateStart(State.MOVING_TO_DESTINATION);
            System.out.println("doing it");
        }

        void setDestination(int destinationFloor) {
            startMoveToDestinationTime = System.currentTimeMillis();
            startFloorLocation = currentFloorLocation;
            this.destinationFloor = destinationFloor;
        }

        public int getDestination() {
            return destinationFloor;
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

    public void onFloorRequest(int floor) {
        notifyOnButtonPress(floor);
    }

    private void notifyOnStateStart(State state) {
        for (Listener listener : listeners) {
            listener.onStateStart(this, state);
        }
    }

    private void notifyOnStateFinish(State state) {
        for (Listener listener : listeners) {
            listener.onStateFinish(this, state);
        }
    }

    private void notifyOnButtonPress(int floor) {
        for (Listener listener : listeners) {
            listener.onButtonPress(this, floor);
        }
    }

    public interface Listener {
        public void onStateStart(Elevator elevator, State state);
        public void onStateFinish(Elevator elevator, State state);
        public void onButtonPress(Elevator elevator, int floor);
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
