package com.gms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateVehicleDto {
    String vehicleNumber;
    String vehicleType;
    String brand;
    String model;
    String manufactureYear;
    String fuelType;
}
