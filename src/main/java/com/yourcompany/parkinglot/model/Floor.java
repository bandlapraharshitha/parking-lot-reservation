package com.yourcompany.parkinglot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Floor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int floorNumber;
}