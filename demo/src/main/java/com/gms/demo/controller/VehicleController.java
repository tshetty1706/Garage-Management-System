package com.gms.demo.controller;

import com.gms.demo.dto.UpdateVehicleDto;
import com.gms.demo.dto.VehicleListDto;
import com.gms.demo.model.Activity;
import com.gms.demo.model.ServiceBooking;
import com.gms.demo.model.User;
import com.gms.demo.model.Vehicle;
import com.gms.demo.repository.ActivityRepo;
import com.gms.demo.repository.BookingRepo;
import com.gms.demo.repository.UserRepo;
import com.gms.demo.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")

public class VehicleController {

    @Autowired
    VehicleRepo vehicleRepo;

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    ActivityRepo activityRepo;

    @Autowired
    UserRepo userRepo;

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



    @GetMapping("/api/admin/vehicles")
    public List<VehicleListDto> getAllVehicles() {

        List<Vehicle> vehicles = vehicleRepo.findAll();
        List<VehicleListDto> response = new ArrayList<>();

        for (Vehicle v : vehicles) {

            VehicleListDto dto = new VehicleListDto();
            dto.setId(v.getVehicleId());
            dto.setVehicleNumber(v.getVehicleNumber());
            dto.setModel(v.getModel());
            dto.setVehicleType(v.getVehicleType());
            dto.setFuelType(v.getFuelType());
            dto.setManufactureYear(v.getManufactureYear());

            Optional<ServiceBooking> latestBooking =
                    bookingRepo.findTopByVehicleIdOrderByBookDateDesc(v.getVehicleId());

            if (latestBooking.isPresent()) {

                String status = latestBooking.get().getStatus();
                dto.setServiceStatus(status);

                if (status.equals("COMPLETED")) {
                    dto.setCanDelete(true);
                } else {
                    dto.setCanDelete(false);
                }

            } else {
                // No service history
                dto.setServiceStatus("NO SERVICE");
                dto.setCanDelete(true);
            }

            response.add(dto);
        }

        return response;
    }

    @DeleteMapping("/api/admin/vehicle/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable int id, @RequestParam int adminId) {

        Optional<ServiceBooking> latestBooking =
                bookingRepo.findTopByVehicleIdOrderByBookDateDesc(id);

        if (latestBooking.isPresent()) {

            String status = latestBooking.get().getStatus();

            if (status.equalsIgnoreCase("ACTIVE") ||
                    status.equalsIgnoreCase("PENDING")) {
                return ResponseEntity
                        .badRequest()
                        .body("Cannot delete vehicle with ACTIVE or PENDING service");
            }
        }

        Vehicle vehicle = vehicleRepo.findByVehicleId(id);
        User user = userRepo.findById(vehicle.getUserId()).orElseThrow(()->new RuntimeException("User not found"));
        User admin = userRepo.findById(adminId).orElseThrow(()->new RuntimeException("User not found"));
        Activity obj = new Activity();
        obj.setUserId(admin.getId());
        obj.setRole(admin.getRole());
        obj.setDescription("Deleted vehicle: '"+vehicle.getVehicleType()+" >> "+vehicle.getBrand()+"( "+vehicle.getVehicleNumber()+")"+"' of user: '"+user.getUsername());
        activityRepo.save(obj);

        Activity obj1 = new Activity();
        obj1.setUserId(user.getId());
        obj1.setRole(user.getRole());
        obj1.setDescription("Your vehicle: '"+vehicle.getVehicleType()+" >> "+vehicle.getBrand()+"( "+vehicle.getVehicleNumber()+")"+"' is deleted by Admin : '"+admin.getUsername());
        activityRepo.save(obj1);


        vehicleRepo.deleteById(id);

        return ResponseEntity.ok("Vehicle deleted successfully");
    }


}
