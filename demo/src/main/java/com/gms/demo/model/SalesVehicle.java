package com.gms.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String vehicleType;

    @Column(nullable = false)
    String model;

    @Column(nullable = false)
    String manufactureYear;

    @Column(nullable = false)
    String brand;

    @Column(nullable = false)
    String fuelType;

    @Column(nullable = false)
    String phone;
}