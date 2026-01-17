package com.gms.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class ServiceBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int bookingId;

    @Column(nullable = false)
    int userId;

    @Column(nullable = false)
    int vehicleId;

    @Column(nullable = false)
    String serviceType; //(general, oil, brake…)

    @Column(nullable = false)
    double price;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    LocalDateTime bookDate;

    @Column(nullable = false)
    String status; //(PENDING / COMPLETED / CANCELLED);
}
