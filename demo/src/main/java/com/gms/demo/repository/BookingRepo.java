package com.gms.demo.repository;

import com.gms.demo.model.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<ServiceBooking,Integer> {
    ServiceBooking findByUserIdAndVehicleIdAndServiceTypeAndStatus(int userId, int vehicleId, String serviceType, String pending);

    int countByUserIdAndStatus(int id, String pending);

    ServiceBooking findTopByUserIdAndStatusOrderByBookDateDesc(int id, String completed);

    List<ServiceBooking> findByVehicleIdOrderByBookDateDesc(int vehicleId);

    boolean existsByVehicleIdAndStatus(int vehicleId, String completed);

    ServiceBooking findByVehicleIdAndStatus(int vehicleId, String pending);
}
