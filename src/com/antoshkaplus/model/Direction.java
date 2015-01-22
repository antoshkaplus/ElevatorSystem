package com.antoshkaplus.model;

/**
 * Created by antoshkaplus on 1/18/15.
 */
public enum Direction {
    UP,
    DOWN,
    NONE;

    static Direction get(int from, int to) {
        if (from == to) return NONE;
        return from > to ? DOWN : UP;
    }
}
