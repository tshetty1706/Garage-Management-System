package com.gms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class BookServiceDto {

    int userId;
    int vehicleId;
    String serviceType;
    double price;

}
