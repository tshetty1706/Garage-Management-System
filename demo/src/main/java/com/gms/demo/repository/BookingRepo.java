package com.gms.demo.repository;

import com.gms.demo.model.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepo extends JpaRepository<ServiceBooking,Integer> {
    ServiceBooking findByUserIdAndVehicleIdAndServiceTypeAndStatus(int userId, int vehicleId, String serviceType, String pending);

    int countByUserIdAndStatus(int id, String pending);

    ServiceBooking findTopByUserIdAndStatusOrderByBookDateDesc(int id, String completed);
}
