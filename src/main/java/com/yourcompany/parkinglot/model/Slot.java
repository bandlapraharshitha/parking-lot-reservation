package com.yourcompany.parkinglot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;

    private int slotNumber;
    private VehicleType vehicleType;
}