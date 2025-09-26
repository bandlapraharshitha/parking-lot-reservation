package com.yourcompany.parkinglot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    private String vehicleNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double cost;

    @Version
    private Long version; // for optimistic locking
}