package com.parkinglot.command;

import com.parkinglot.model.Ticket;
import com.parkinglot.model.Vehicle;
import com.parkinglot.model.VehicleType;
import com.parkinglot.service.ParkingLotService;

import java.util.List;

public class CommandHandler {
    private final ParkingLotService parkingLotService;

    public CommandHandler() {
        this.parkingLotService = new ParkingLotService();
    }

    public void handle(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] parts = input.trim().split("\\s+");
        String command = parts[0];

        switch (command) {
            case "create_parking_lot":
                handleCreateParkingLot(parts);
                break;
            case "park_vehicle":
                handleParkVehicle(parts);
                break;
            case "unpark_vehicle":
                handleUnparkVehicle(parts);
                break;
            case "display":
                handleDisplay(parts);
                break;
            case "exit":
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    private void handleCreateParkingLot(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Invalid command");
            return;
        }
        String parkingLotId = parts[1];
        int numberOfFloors = Integer.parseInt(parts[2]);
        int slotsPerFloor = Integer.parseInt(parts[3]);
        parkingLotService.createParkingLot(parkingLotId, numberOfFloors, slotsPerFloor);
        System.out.println("Created parking lot with " + numberOfFloors + " floors and " + slotsPerFloor + " slots per floor");
    }

    private void handleParkVehicle(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Invalid command");
            return;
        }
        VehicleType vehicleType = VehicleType.valueOf(parts[1]);
        String regNo = parts[2];
        String color = parts[3];

        Ticket ticket = parkingLotService.parkVehicle(vehicleType, regNo, color);
        if (ticket == null) {
            System.out.println("Parking Lot Full");
        } else {
            System.out.println("Parked vehicle. Ticket ID: " + ticket.getTicketId());
        }
    }

    private void handleUnparkVehicle(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Invalid command");
            return;
        }
        String ticketId = parts[1];
        Vehicle vehicle = parkingLotService.unparkVehicle(ticketId);
        if (vehicle == null) {
            System.out.println("Invalid Ticket");
        } else {
            System.out.println("Unparked vehicle with Registration Number: " + vehicle.getRegistrationNumber()
                    + " and Color: " + vehicle.getColor());
        }
    }

    private void handleDisplay(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Invalid command");
            return;
        }
        String displayType = parts[1];
        VehicleType vehicleType = VehicleType.valueOf(parts[2]);

        List<String> result;
        switch (displayType) {
            case "free_count":
                result = parkingLotService.displayFreeCount(vehicleType);
                break;
            case "free_slots":
                result = parkingLotService.displayFreeSlots(vehicleType);
                break;
            case "occupied_slots":
                result = parkingLotService.displayOccupiedSlots(vehicleType);
                break;
            default:
                System.out.println("Invalid display type");
                return;
        }

        for (String line : result) {
            System.out.println(line);
        }
    }
}
