package com.parkinglot.model;

import java.util.ArrayList;
import java.util.List;

public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSlot> slots;

    public ParkingFloor(int floorNumber, int numberOfSlots) {
        this.floorNumber = floorNumber;
        this.slots = new ArrayList<>();
        initializeSlots(numberOfSlots);
    }

    private void initializeSlots(int numberOfSlots) {
        for (int i = 1; i <= numberOfSlots; i++) {
            VehicleType slotType = determineSlotType(i);
            ParkingSlot slot = new ParkingSlot(i, slotType);
            slot.setFloorNumber(floorNumber);
            slots.add(slot);
        }
    }

    private VehicleType determineSlotType(int slotNumber) {
        if (slotNumber == 1) {
            return VehicleType.TRUCK;
        } else if (slotNumber == 2 || slotNumber == 3) {
            return VehicleType.BIKE;
        } else {
            return VehicleType.CAR;
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }
}
