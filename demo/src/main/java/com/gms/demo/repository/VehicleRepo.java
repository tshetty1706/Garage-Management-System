package com.gms.demo.repository;

import com.gms.demo.model.Vehicle;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle,Integer> {

    List<Vehicle> findAllByUserId(int userId);

    Vehicle findByVehicleNumber(String vehicleNumber);

    int countByUserId(int id);

    Vehicle findByVehicleId(int vehicleId);
}
