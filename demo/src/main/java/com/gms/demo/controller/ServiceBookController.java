package com.gms.demo.controller;

import com.gms.demo.dto.BookServiceDto;
import com.gms.demo.model.Activity;
import com.gms.demo.model.ServiceBooking;
import com.gms.demo.model.Vehicle;
import com.gms.demo.repository.ActivityRepo;
import com.gms.demo.repository.BookingRepo;
import com.gms.demo.repository.UserRepo;
import com.gms.demo.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

public class ServiceBookController {

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    VehicleRepo vehicleRepo;

    @Autowired
    ActivityRepo activityRepo;

    @GetMapping("vehicles/user/{userId}")
    public List<Vehicle> selectVehicle(@PathVariable int userId)
    {
        return vehicleRepo.findAllByUserId(userId);
    }

    @PostMapping("/service-bookings")
    public String bookService(@RequestBody BookServiceDto bookServiceDto)
    {
        ServiceBooking obj = bookingRepo.findByUserIdAndVehicleIdAndServiceTypeAndStatus(bookServiceDto.getUserId(),bookServiceDto.getVehicleId(),bookServiceDto.getServiceType(),"PENDING");
        Vehicle vehicle = vehicleRepo.findByVehicleId(bookServiceDto.getVehicleId());

        if(obj!=null)
        {
            Activity activity = new Activity();
            activity.setUserId(bookServiceDto.getUserId());
            activity.setDescription("Failed Booking. Service for "+vehicle.getVehicleNumber()+" is already booked and the Service is going on");
            activity.setRole("CUSTOMER");
            activityRepo.save(activity);

            return "Service is already booked and the Service is going on";
        }

        ServiceBooking serviceBooking = new ServiceBooking();

        serviceBooking.setUserId(bookServiceDto.getUserId());
        serviceBooking.setVehicleId(bookServiceDto.getVehicleId());
        serviceBooking.setServiceType(bookServiceDto.getServiceType());
        serviceBooking.setPrice(bookServiceDto.getPrice());
        serviceBooking.setStatus("PENDING");


        Activity activity = new Activity();
        activity.setUserId(bookServiceDto.getUserId());
        activity.setDescription("Booked a service "+serviceBooking.getServiceType()+" for "+vehicle.getVehicleNumber());
        activity.setRole("CUSTOMER");
        activity.setUserId(bookServiceDto.getUserId());
        activityRepo.save(activity);

        bookingRepo.save(serviceBooking);
        return "Service booked successfully";
    }
}
