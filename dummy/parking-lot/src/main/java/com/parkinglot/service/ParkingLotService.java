package com.parkinglot.service;

import com.parkinglot.model.*;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotService {
    private ParkingLot parkingLot;

    public void createParkingLot(String parkingLotId, int numberOfFloors, int slotsPerFloor) {
        this.parkingLot = new ParkingLot(parkingLotId, numberOfFloors, slotsPerFloor);
    }

    public Ticket parkVehicle(VehicleType vehicleType, String registrationNumber, String color) {
        if (parkingLot == null) {
            return null;
        }

        ParkingSlot availableSlot = findFirstAvailableSlot(vehicleType);
        if (availableSlot == null) {
            return null;
        }

        Vehicle vehicle = new Vehicle(vehicleType, registrationNumber, color);
        availableSlot.parkVehicle(vehicle);

        String ticketId = parkingLot.getParkingLotId() + "_" +
                availableSlot.getFloorNumber() + "_" +
                availableSlot.getSlotNumber();

        return new Ticket(ticketId, vehicle);
    }

    public Vehicle unparkVehicle(String ticketId) {
        if (parkingLot == null) {
            return null;
        }

        String[] parts = ticketId.split("_");
        if (parts.length != 3) {
            return null;
        }

        String lotId = parts[0];
        int floorNumber;
        int slotNumber;

        try {
            floorNumber = Integer.parseInt(parts[1]);
            slotNumber = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }

        if (!lotId.equals(parkingLot.getParkingLotId())) {
            return null;
        }

        ParkingFloor floor = getFloor(floorNumber);
        if (floor == null) {
            return null;
        }

        ParkingSlot slot = getSlot(floor, slotNumber);
        if (slot == null || slot.isAvailable()) {
            return null;
        }

        Vehicle vehicle = slot.getParkedVehicle();
        slot.unparkVehicle();
        return vehicle;
    }

    public List<String> displayFreeCount(VehicleType vehicleType) {
        List<String> result = new ArrayList<>();
        for (ParkingFloor floor : parkingLot.getFloors()) {
            long count = floor.getSlots().stream()
                    .filter(slot -> slot.getSlotType() == vehicleType)
                    .filter(ParkingSlot::isAvailable)
                    .count();
            result.add("No. of free slots for " + vehicleType + " on Floor " + floor.getFloorNumber() + ": " + count);
        }
        return result;
    }

    public List<String> displayFreeSlots(VehicleType vehicleType) {
        List<String> result = new ArrayList<>();
        for (ParkingFloor floor : parkingLot.getFloors()) {
            List<String> slots = new ArrayList<>();
            for (ParkingSlot slot : floor.getSlots()) {
                if (slot.getSlotType() == vehicleType && slot.isAvailable()) {
                    slots.add(String.valueOf(slot.getSlotNumber()));
                }
            }
            result.add("Free slots for " + vehicleType + " on Floor " + floor.getFloorNumber() + ": " + String.join(",", slots));
        }
        return result;
    }

    public List<String> displayOccupiedSlots(VehicleType vehicleType) {
        List<String> result = new ArrayList<>();
        for (ParkingFloor floor : parkingLot.getFloors()) {
            List<String> slots = new ArrayList<>();
            for (ParkingSlot slot : floor.getSlots()) {
                if (slot.getSlotType() == vehicleType && !slot.isAvailable()) {
                    slots.add(String.valueOf(slot.getSlotNumber()));
                }
            }
            result.add("Occupied slots for " + vehicleType + " on Floor " + floor.getFloorNumber() + ": " + String.join(",", slots));
        }
        return result;
    }

    private ParkingSlot findFirstAvailableSlot(VehicleType vehicleType) {
        for (ParkingFloor floor : parkingLot.getFloors()) {
            for (ParkingSlot slot : floor.getSlots()) {
                if (slot.getSlotType() == vehicleType && slot.isAvailable()) {
                    return slot;
                }
            }
        }
        return null;
    }

    private ParkingFloor getFloor(int floorNumber) {
        for (ParkingFloor floor : parkingLot.getFloors()) {
            if (floor.getFloorNumber() == floorNumber) {
                return floor;
            }
        }
        return null;
    }

    private ParkingSlot getSlot(ParkingFloor floor, int slotNumber) {
        for (ParkingSlot slot : floor.getSlots()) {
            if (slot.getSlotNumber() == slotNumber) {
                return slot;
            }
        }
        return null;
    }
}
