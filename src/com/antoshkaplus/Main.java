package com.antoshkaplus;

import com.antoshkaplus.model.Building;
import com.antoshkaplus.model.Elevator;
import com.antoshkaplus.model.RequestMaker;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {

        Application.launch(Visualizer.class, args);

        // 6 floors and 4 elevators
//        Building building = new Building(6, 4);
//        com.antoshkaplus.model.ElevatorSystem model = new com.antoshkaplus.model.ElevatorSystem(building);
//        for (Elevator elevator : building.getElevators()) {
//            model.addElevator(elevator);
//        }
//        RequestMaker requestMaker = new RequestMaker();
//        requestMaker.setElevatorSystem(model);
//        requestMaker.start();

        // now just gather updates you need

    }
}
