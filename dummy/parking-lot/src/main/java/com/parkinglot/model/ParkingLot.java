package com.parkinglot.model;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private final String parkingLotId;
    private final List<ParkingFloor> floors;

    public ParkingLot(String parkingLotId, int numberOfFloors, int slotsPerFloor) {
        this.parkingLotId = parkingLotId;
        this.floors = new ArrayList<>();
        for (int i = 1; i <= numberOfFloors; i++) {
            floors.add(new ParkingFloor(i, slotsPerFloor));
        }
    }

    public String getParkingLotId() {
        return parkingLotId;
    }

    public List<ParkingFloor> getFloors() {
        return floors;
    }
}
