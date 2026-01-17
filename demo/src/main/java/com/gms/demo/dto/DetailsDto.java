package com.gms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DetailsDto {
    String userName;
    int vehicleCount;
    int activeServiceCount;

    String lastServiceType;
    String VehicleNumber;
    LocalDateTime date;
}
