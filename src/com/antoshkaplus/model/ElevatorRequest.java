package com.antoshkaplus.model;

/**
 * Created by antoshkaplus on 1/18/15.
 */
public class ElevatorRequest {
    public int floor;
    public Direction direction;

    public ElevatorRequest(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
    }
}
