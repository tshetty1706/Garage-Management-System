package com.gms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class VehicleListDto {

    int id;
    String vehicleNumber;
    String model;
    String vehicleType;
    String fuelType;
    String manufactureYear;

    String serviceStatus;
    boolean canDelete;
}
