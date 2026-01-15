package com.gms.demo.repository;

import com.gms.demo.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepo extends JpaRepository<Vehicle,Integer> {
    Vehicle findByVehicleNumber(String vehicleNumber);
}
