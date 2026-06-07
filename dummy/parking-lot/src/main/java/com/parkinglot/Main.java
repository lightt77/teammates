package com.parkinglot;

import com.parkinglot.command.CommandHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CommandHandler handler = new CommandHandler();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                break;
            }
            handler.handle(line);
        }

        scanner.close();
    }
}
