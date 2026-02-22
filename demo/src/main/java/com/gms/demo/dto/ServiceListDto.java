package com.gms.demo.dto;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ServiceListDto {

        int bookingId;
        String vehicleNumber;
        String vehicleModel;
        String serviceType;
        LocalDateTime bookDate;
        String status;

}
