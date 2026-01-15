package com.gms.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int vehicle_id;  //pk

    @Column(nullable = false)
    int userId; //fk  the owner of vehicle

    @Column(nullable = false,unique = true)
    String vehicleNumber;

    @Column(nullable = false)
    String vehicleType; //car,bike,scooter,

    @Column(nullable = false)
    String brand; //Honda, Hyundai, Tata, Suzuki

    @Column(nullable = false)
    String model; //City, i20, Nexon

    @Column(nullable = false)
    String fuelType; //Petrol, Diesel ,Electric,CNG (drop down)

    @Column(nullable = false)
    String manufactureYear;


    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    LocalDateTime createdAt;

}
