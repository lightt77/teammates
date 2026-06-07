package com.parkinglot.model;

public class ParkingSlot {
    private final int slotNumber;
    private final VehicleType slotType;
    private int floorNumber;
    private Vehicle parkedVehicle;

    public ParkingSlot(int slotNumber, VehicleType slotType) {
        this.slotNumber = slotNumber;
        this.slotType = slotType;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public VehicleType getSlotType() {
        return slotType;
    }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public void parkVehicle(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
    }

    public void unparkVehicle() {
        this.parkedVehicle = null;
    }
}
