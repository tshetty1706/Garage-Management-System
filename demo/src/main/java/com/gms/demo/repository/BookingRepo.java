package com.gms.demo.repository;

import com.gms.demo.model.ServiceBooking;
import com.gms.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<ServiceBooking,Integer> {
    ServiceBooking findByUserIdAndVehicleIdAndServiceTypeAndStatus(int userId, int vehicleId, String serviceType, String pending);

    int countByUserIdAndStatus(int id, String pending);

    ServiceBooking findTopByUserIdAndStatusOrderByBookDateDesc(int id, String completed);

    List<ServiceBooking> findByVehicleIdOrderByBookDateDesc(int vehicleId);

    boolean existsByVehicleIdAndStatus(int vehicleId, String completed);

    ServiceBooking findByVehicleIdAndStatus(int vehicleId, String pending);

    int countByStatus(String active);

    @Query("SELECT COALESCE(SUM(b.price), 0) FROM ServiceBooking b WHERE b.status = 'COMPLETED'")
    double getTotalRevenue();

    List<ServiceBooking> findTop5ByOrderByBookDateDesc();


    boolean existsByUserIdAndStatus(int id, String active);

    boolean existsByUserId(int id);

    Optional<ServiceBooking> findTopByVehicleIdOrderByBookDateDesc(int vehicleId);
}
