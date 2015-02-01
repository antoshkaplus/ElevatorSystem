package com.antoshkaplus.model;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * Created by antoshkaplus on 1/18/15.
 *
 * should push events that create new requests
 */
public class Population implements Elevator.StateListener {
    private Building building;

    // ids of users that are free and floor
    private ArrayList<IdleUser> idleUsers = new ArrayList<IdleUser>();
    // how many idle users per floor
    private int[] idleUsersPerFloor;
    private int idleUsersCount = 0;

    private ArrayList<EnumMap<Direction, ArrayList<WaitingUser>>> waitingUsers = new ArrayList<EnumMap<Direction, ArrayList<WaitingUser>>>();
    private int waitingUsersCount = 0;

    // by elevator
    private Map<Integer, ArrayList<TravelingUser>> travelingUsers = new HashMap<Integer, ArrayList<TravelingUser>>();
    private int travelingUsersCount = 0;

    // measured in milliseconds
    private int requestFrequency;
    private Random random = new Random();

    private static final int elevatorCapacity = 10;

    Object operations = new Object();

    public Population(Building building, int count, int requestFrequency) {
        this.building = building;
        for (BuildingElevator bel : building.getElevators()) {
            bel.addStateListener(this);
        }

        this.idleUsersCount = count;
        for (int i = 0; i < count; ++i) {
            // all start from 0 floor
            idleUsers.add(new IdleUser(i, 0));

            EnumMap<Direction, ArrayList<WaitingUser>> ws = new EnumMap<Direction, ArrayList<WaitingUser>>(Direction.class);
            ws.put(Direction.DOWN, new ArrayList<WaitingUser>());
            ws.put(Direction.UP, new ArrayList<WaitingUser>());
            waitingUsers.add(ws);
        }
        idleUsersPerFloor = new int[building.getFloorCount()];
        Arrays.fill(idleUsersPerFloor, 0);
        idleUsersPerFloor[0] = count;

        for (Elevator e : building.getElevators()) {
            travelingUsers.put(e.getElevatorId(), new ArrayList<TravelingUser>());
        }

        this.requestFrequency = requestFrequency;
    }

    public void run() {
        while (true) {
            int time = random.nextInt(2 * requestFrequency) + 1;
            try {
                // must be like this for a reason
                synchronized (this) {
                    wait(time);
                }
            } catch (InterruptedException e) {
            }
            IdleUser u;
            Direction d;
            synchronized (operations) {
                if (idleUsersCount == 0) continue;
                int i = random.nextInt(idleUsersCount);
                u = idleUsers.get(i);
                Collections.swap(idleUsers, i, idleUsersCount - 1);
                idleUsers.remove(idleUsersCount - 1);
                --idleUsersCount;
                --idleUsersPerFloor[u.floor];
                // finding target floor
                int t;
                while ((t = random.nextInt(building.getFloorCount())) == u.floor) ;
                d = Direction.get(u.floor, t);
                waitingUsers.get(u.floor).get(d).add(new WaitingUser(u.id, t));
                ++waitingUsersCount;
            }
            building.onRequest(new ElevatorRequest(u.floor, d));
        }
    }

    public int getWaitingUserCount(int floor, Direction direction) {
        return waitingUsers.get(floor).get(direction).size();
    }

    public int getWaitingUserCount() {
        return waitingUsersCount;
    }

    public int getIdleUserCount(int floor) {
        return idleUsersPerFloor[floor];
    }

    public int getIdleUserCount() {
        return idleUsersCount;
    }

    public int getTravelingUserCount(int elevatorId) {
        return travelingUsers.get(elevatorId).size();
    }

    public int getTravelingUserCount() {
        return travelingUsersCount;
    }

    @Override
    public void onStateStart(Elevator elevator, Elevator.State state) {
        BuildingElevator bel = (BuildingElevator)elevator;
        if (!bel.getControls().isFunctional()) return;
        if (state != Elevator.State.PASSENGER_TRANSFER) return;
        performTransfer(bel);
    }

    @Override
    public void onStateFinish(Elevator elevator, Elevator.State state) {
        BuildingElevator bel = (BuildingElevator)elevator;
        if (!bel.getControls().isFunctional()) return;
        if (state != Elevator.State.PASSENGER_TRANSFER) return;
        performTransfer(bel);
    }

    public synchronized void performTransfer(BuildingElevator bel) {
        int floor = bel.getFloor();
        ArrayList<TravelingUser> ts = travelingUsers.get(bel.getElevatorId());
        for (int i = 0; i < ts.size(); ) {
            if (ts.get(i).targetFloor == floor) {
                ++idleUsersCount;
                ++idleUsersPerFloor[floor];
                idleUsers.add(new IdleUser(ts.get(i).id, floor));

                Collections.swap(ts, i, ts.size() - 1);
                ts.remove(ts.size() - 1);
                --travelingUsersCount;
            } else {
                ++i;
            }
        }

        // getting in some users
        ArrayList<WaitingUser> ws = waitingUsers.get(floor).get(bel.getControls().getDirection());
        // how much space left in the elevator
        int leftSpace = Math.max(elevatorCapacity - ts.size(), 0);
        int countIn = Math.min(leftSpace, ws.size());
        for (int i = 0; i < countIn; ++i) {
            int k = ws.size()-1;
            WaitingUser w = ws.get(k);
            ts.add(new TravelingUser(w.id, w.targetFloor));
            bel.PressButton(w.targetFloor);
            ws.remove(k);
        }
        travelingUsersCount += countIn;
        waitingUsersCount -= countIn;
        if (ws.size() > 0) building.onRequest(new ElevatorRequest(floor, bel.getControls().getDirection()));
    }

    class User {
        User(int id) {
            this.id = id;
        }
        int id;
    }

    class IdleUser extends User {
        IdleUser(int userId, int floor) {
            super(userId);
            this.floor = floor;
        }
        int floor;
    }

    class WaitingUser extends User {
        WaitingUser(int userId, int targetFloor) {
            super(userId);
            this.targetFloor = targetFloor;
        }
        int targetFloor;
    }

    class TravelingUser extends User {
        TravelingUser(int userId, int targetFloor) {
            super(userId);
            this.targetFloor = targetFloor;
        }
        int targetFloor;
    }
}
