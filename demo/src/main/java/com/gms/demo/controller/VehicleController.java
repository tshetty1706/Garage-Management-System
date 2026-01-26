package com.gms.demo.controller;

import com.gms.demo.dto.UpdateVehicleDto;
import com.gms.demo.model.Activity;
import com.gms.demo.model.ServiceBooking;
import com.gms.demo.model.Vehicle;
import com.gms.demo.repository.ActivityRepo;
import com.gms.demo.repository.BookingRepo;
import com.gms.demo.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")

public class VehicleController {

    @Autowired
    VehicleRepo vehicleRepo;

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    ActivityRepo activityRepo;

    @GetMapping("/api/vehicles/{vehicleId}")
    public Vehicle getVehicleById(@PathVariable int vehicleId) {
        return vehicleRepo.findById(vehicleId).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }


    @PostMapping("/api/vehicles/{vehicleId}")
    public String updateVehicle(@RequestBody UpdateVehicleDto obj,@PathVariable int vehicleId)
    {
        Activity activity = new Activity();
        ServiceBooking hasPending = bookingRepo.findByVehicleIdAndStatus(vehicleId,"PENDING");

        if(hasPending!=null)
        {
            return "Cannot edit vehicle while service is active.";
        }

        Vehicle vehicle = vehicleRepo.findByVehicleId(vehicleId);

        boolean isSame = vehicle.getVehicleNumber().equalsIgnoreCase(obj.getVehicleNumber()) &&
                vehicle.getVehicleType().equalsIgnoreCase(obj.getVehicleType()) &&
                vehicle.getBrand().equalsIgnoreCase(obj.getBrand()) &&
                vehicle.getModel().equalsIgnoreCase(obj.getModel()) &&
                vehicle.getManufactureYear().equals(obj.getManufactureYear()) &&
                vehicle.getFuelType().equalsIgnoreCase(obj.getFuelType());
        if(isSame)
        {
            return "No changes detected";
        }

        Vehicle vehicle1 = vehicleRepo.findByVehicleNumber(obj.getVehicleNumber());
        //vehicle1 != null && vehicle1.getVehicleId()!= vehicleId
        if(vehicle1 != null && vehicle1.getVehicleId()!= vehicleId)
        {
            return "Vehicle number already exists";
        }

        vehicle.setVehicleNumber(obj.getVehicleNumber());
        vehicle.setVehicleType(obj.getVehicleType());
        vehicle.setBrand(obj.getBrand());
        vehicle.setModel(obj.getModel());
        vehicle.setManufactureYear(obj.getManufactureYear());
        vehicle.setFuelType(obj.getFuelType());
        vehicleRepo.save(vehicle);

        activity.setDescription("Vehicle updated successfully!");
        activity.setRole("CUSTOMER");
        activity.setUserId(vehicle.getUserId());
        activityRepo.save(activity);

        return "Vehicle updated successfully";
    }
}
