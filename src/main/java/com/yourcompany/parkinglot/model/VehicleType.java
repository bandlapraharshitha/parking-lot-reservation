package com.yourcompany.parkinglot.model;

public enum VehicleType {
    FOUR_WHEELER(30.0),
    TWO_WHEELER(20.0);

    private final double hourlyRate;

    VehicleType(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}