package com.antoshkaplus.model;

import javax.jws.soap.SOAPBinding;
import java.util.*;

/**
 * Created by antoshkaplus on 1/18/15.
 */
public class Population extends Thread {
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

    public Population(Building building, int count, int requestFrequency) {
        this.building = building;
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

    @Override
    public void run() {
        while (true) {
            int time = random.nextInt(2*requestFrequency);
            try {
                synchronized (this) {
                    wait(time);
                }
            } catch (InterruptedException e) {
            }
            if (idleUsersCount == 0) continue;
            int i = random.nextInt(idleUsersCount);
            IdleUser u = idleUsers.get(i);
            Collections.swap(idleUsers, i, idleUsersCount-1);
            idleUsers.remove(idleUsersCount-1);
            --idleUsersCount;
            --idleUsersPerFloor[u.floor];
            // finding target floor
            int t;
            while ((t = random.nextInt(building.getFloorCount())) == u.floor);
            Direction d = Direction.get(u.floor, t);
            waitingUsers.get(u.floor).get(d).add(new WaitingUser(u.id, t));
            ++waitingUsersCount;
            building.onElevatorRequest(new ElevatorRequest(u.floor, d));
        }
    }

    // probably should be a thread..

    // is going to need some sort of event


    // should be able to get floor from elevator some how ???
    void onTransfer(int elevatorId, Direction elevatorDirection) {
        Elevator e = building.getElevator(elevatorId);
        int floor = e.getFloor();
        // getting out some users
        ArrayList<TravelingUser> ts = travelingUsers.get(e.getElevatorId());
        for (int i = 0; i < ts.size();) {
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
        ArrayList<WaitingUser> ws = waitingUsers.get(floor).get(elevatorDirection);
        for (WaitingUser w : ws) {
            ts.add(new TravelingUser(w.id, w.targetFloor));
            e.onFloorRequest(w.targetFloor);
        }
        travelingUsersCount += ws.size();
        waitingUsersCount -= ws.size();
        ws.clear();

    }

    int getWaitingUserCount(int floor, Direction direction) {
        return waitingUsers.get(floor).get(direction).size();
    }

    int getWaitingUserCount() {
        return waitingUsersCount;
    }

    int getIdleUserCount(int floor) {
        return idleUsersPerFloor[floor];
    }

    int getIdleUserCount() {
        return idleUsersCount;
    }

    int getTravelingUserCount(int elevatorId) {
        return travelingUsers.get(elevatorId).size();
    }

    int getTravelingUserCount() {
        return travelingUsersCount;
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
