package com.gms.demo.controller;

import com.gms.demo.dto.BookServiceDto;
import com.gms.demo.dto.ServiceListDto;
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
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")

public class ServiceBookController {

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    VehicleRepo vehicleRepo;

    @Autowired
    ActivityRepo activityRepo;

    @Autowired
    UserRepo userRepo;

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


    @GetMapping("/api/admin/services")
    public List<ServiceListDto> getAllServices() {

        List<ServiceBooking> bookings = bookingRepo.findAll();
        List<ServiceListDto> response = new ArrayList<>();

        for (ServiceBooking booking : bookings) {

            ServiceListDto dto = new ServiceListDto();

            dto.setBookingId(booking.getBookingId());
            dto.setServiceType(booking.getServiceType());
            dto.setBookDate(booking.getBookDate());
            dto.setStatus(booking.getStatus());

            // Fetch vehicle details
            Optional<Vehicle> vehicle =
                    vehicleRepo.findById(booking.getVehicleId());

            if (vehicle.isPresent()) {
                dto.setVehicleNumber(vehicle.get().getVehicleNumber());
                dto.setVehicleModel(vehicle.get().getModel());
            }


            response.add(dto);
        }

        return response;
    }

    @PutMapping("/api/admin/service/{id}/status")
    public ResponseEntity<String> updateServiceStatus(
            @PathVariable int id,
            @RequestParam int adminId,
            @RequestBody Map<String, String> body) {

        ServiceBooking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        String newStatus = body.get("status");

        if (!List.of("PENDING", "ACTIVE", "COMPLETED").contains(newStatus)) {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        booking.setStatus(newStatus);
        bookingRepo.save(booking);

        User admin = userRepo.findById(adminId).orElseThrow(()->new RuntimeException("Admin not found"));
        ServiceBooking user = bookingRepo.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        Vehicle vehicle = vehicleRepo.findByVehicleId(user.getVehicleId());

        Activity objAdmin = new Activity();
        Activity objUser = new Activity();

        objAdmin.setUserId(admin.getId());
        objAdmin.setRole(admin.getRole());
        objAdmin.setDescription("Status of vehicle '"+vehicle.getVehicleType()+"( "+vehicle.getVehicleNumber()+" )"+"' :"+newStatus);
        activityRepo.save(objAdmin);

        objUser.setUserId(user.getUserId());
        objUser.setDescription("Your vehicle '"+vehicle.getVehicleType()+"( "+vehicle.getVehicleNumber()+" )"+"'service status is updated to: "+newStatus);
        objUser.setRole("CUSTOMER");
        activityRepo.save(objUser);

        return ResponseEntity.ok("Service status updated");
    }
}
