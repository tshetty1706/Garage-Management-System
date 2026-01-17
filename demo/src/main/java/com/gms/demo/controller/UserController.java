package com.gms.demo.controller;

import com.gms.demo.dto.CheckLogin;
import com.gms.demo.dto.DetailsDto;
import com.gms.demo.dto.LoginDto;
import com.gms.demo.model.ServiceBooking;
import com.gms.demo.model.User;
import com.gms.demo.model.Vehicle;
import com.gms.demo.repository.BookingRepo;
import com.gms.demo.repository.UserRepo;
import com.gms.demo.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@RestController
@CrossOrigin(origins = "*")

public class UserController {
    @Autowired
    UserRepo userRepo;

    @Autowired
    VehicleRepo vehicleRepo;

    @Autowired
    BookingRepo bookingRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user)
    {
        User u1 = userRepo.findByEmailOrUsername(user.getEmail(), user.getPassword());
        if(u1!=null)
        {
            return "User already exists. Redirecting to login...";
        }

        user.setRole("CUSTOMER");
        userRepo.save(user);
        return "Signup Successfully";
    }

    @PostMapping("/login")
    public CheckLogin login(@RequestBody LoginDto loginDto)
    {
        User user = userRepo.findByUsername(loginDto.getUsername());

        if(user==null || !user.getPassword().equalsIgnoreCase(loginDto.getPassword())) return new CheckLogin();

        CheckLogin checkLogin = new CheckLogin();
        checkLogin.setUserId(user.getId());
        checkLogin.setRole(user.getRole());

        return checkLogin;
    }

    @GetMapping("/api/users/{id}")
    public DetailsDto details(@PathVariable int id)
    {
        DetailsDto detailsDto = new DetailsDto();
        User user =  userRepo.findById(id).orElseThrow(()->new RuntimeException("User not found"));

        int activeServiceCount = bookingRepo.countByUserIdAndStatus(id,"PENDING");
        int vehicleCount = vehicleRepo.countByUserId(id);
        ServiceBooking serviceBooking = bookingRepo.findTopByUserIdAndStatusOrderByBookDateDesc(id, "COMPLETED");


        detailsDto.setUserName(user.getUsername());
        detailsDto.setVehicleCount(vehicleCount);
        detailsDto.setActiveServiceCount(activeServiceCount);

        if(serviceBooking !=null)
        {
            detailsDto.setLastServiceType(serviceBooking.getServiceType());
            detailsDto.setDate(serviceBooking.getBookDate());

            Vehicle vehicle = vehicleRepo.findByVehicleId(serviceBooking.getVehicleId());
            if(vehicle != null)
            {
                detailsDto.setVehicleNumber(vehicle.getVehicleNumber());
            }
        }


        return detailsDto;
    }

    @PostMapping("/vehicles")
    public String addVehicle(@RequestBody Vehicle vehicle, @RequestParam int userId) {
        Vehicle existing = vehicleRepo.findByVehicleNumber(vehicle.getVehicleNumber());
        if (existing != null) return "Vehicle number already exists";

        vehicle.setUserId(userId);
        vehicleRepo.save(vehicle);

        return "Vehicle added successfully";
    }

}