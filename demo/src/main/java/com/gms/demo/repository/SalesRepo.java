package com.gms.demo.repository;

import com.gms.demo.model.SalesVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepo extends JpaRepository<SalesVehicle, Integer> {
}
