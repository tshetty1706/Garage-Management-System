package com.gms.demo.controller;

import com.gms.demo.dto.*;
import com.gms.demo.model.Activity;
import com.gms.demo.model.ServiceBooking;
import com.gms.demo.model.User;
import com.gms.demo.model.Vehicle;
import com.gms.demo.repository.ActivityRepo;
import com.gms.demo.repository.BookingRepo;
import com.gms.demo.repository.UserRepo;
import com.gms.demo.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")

public class UserController {
    @Autowired
    UserRepo userRepo;

    @Autowired
    VehicleRepo vehicleRepo;

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    ActivityRepo activityRepo;

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


        Activity activity = new Activity();
        activity.setUserId(user.getId());
        activity.setDescription("Welcome "+user.getUsername()+" to GMS! Your account has been created successfully.");
        activity.setRole("CUSTOMER");

        activityRepo.save(activity);

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

        Activity activity = new Activity();
        activity.setUserId(userId);
        activity.setDescription("Added a new Vehicle "+vehicle.getVehicleNumber()+" to the garage");
        activity.setRole("CUSTOMER");
        activityRepo.save(activity);


        return "Vehicle added successfully";
    }


    @GetMapping("/api/vehicles/user/{userId}")
    public List<Vehicle> getUserVehicle(@PathVariable int userId)
    {
        return vehicleRepo.findAllByUserId(userId);
    }




    @GetMapping("/api/service-history/vehicle/{vehicleId}")
    public List<ServiceBooking> getUserServiceHistory(@PathVariable int vehicleId)
    {
        return bookingRepo.findByVehicleIdOrderByBookDateDesc(vehicleId);
    }

    @DeleteMapping("/api/vehicles/{vehicleId}")
    public String deleteVehicle(@PathVariable int vehicleId) {

        boolean hasActiveService =
                bookingRepo.existsByVehicleIdAndStatus(vehicleId, "PENDING");


        Vehicle vehicle = vehicleRepo.findByVehicleId(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found";
        }

        if (hasActiveService) {
            Activity activity = new Activity();
            activity.setUserId(vehicle.getUserId());
            activity.setDescription("Delete Failed. Attempted to delete a vehicle with active services");
            activity.setRole("CUSTOMER");
            activityRepo.save(activity);

            return "Vehicle has active services";
        }

        Activity activity = new Activity();
        activity.setUserId(vehicle.getUserId());
        activity.setDescription(vehicle.getVehicleNumber()+" Deleted successfully");
        activity.setRole("CUSTOMER");
        activityRepo.save(activity);

        vehicleRepo.delete(vehicle);
        return "Vehicle deleted successfully";
    }


    @GetMapping("/api/profile/{userId}")
    public ProfileDto displayProfile(@PathVariable int userId)
    {
        User user = userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));

        ProfileDto profileDto = new ProfileDto();
        profileDto.setName(user.getName());
        profileDto.setUsername(user.getUsername());
        profileDto.setRole(user.getRole());
        profileDto.setPhone(user.getPhone());
        profileDto.setEmail(user.getEmail());

        return profileDto;
    }

    @PostMapping("/api/profile/update")
    public String update(@RequestBody UpdateDto updateDto)
    {
        User user = userRepo.findById(updateDto.getUserId()).orElseThrow(()-> new RuntimeException("User not found"));
        Activity activity = new Activity();

        if(updateDto.getKey().equalsIgnoreCase("name"))
        {
            if(updateDto.getValue().equals(user.getName())) return "Cannot be same";
            user.setName(updateDto.getValue());
            activity.setDescription("Your profile name has been updated successfully");

        } else if (updateDto.getKey().equalsIgnoreCase("password")) {
            if(updateDto.getValue().equalsIgnoreCase(user.getPassword())) return "Cannot be same";
            user.setPassword(updateDto.getValue());
            activity.setDescription("Your account password was changed successfully.");

        } else if(updateDto.getKey().equalsIgnoreCase("email")){
            if(updateDto.getValue().equals(user.getEmail())) return "Cannot be same";

            User u1 = userRepo.findByEmail(updateDto.getValue());
            if(u1 != null)
            {
                return "Email Already Exists";
            }
            user.setEmail(updateDto.getValue());
            activity.setDescription("Your registered email address has been updated successfully.");

        } else if(updateDto.getKey().equalsIgnoreCase("phone")){
            if(updateDto.getValue().length()<10) return "Enter a valid 10-digit phone number";

            User u1 = userRepo.findByPhone(updateDto.getValue());
            if(u1 != null)
            {
                return "Phone number Already Exists";
            }

            if(updateDto.getValue().equals(user.getPhone())) return "Cannot be same";

            user.setPhone(updateDto.getValue());
            activity.setDescription("Your Phone Number has been updated successfully.");
        }
        else{
            return "Invalid key";
        }

        activity.setRole("CUSTOMER");
        activity.setUserId(user.getId());
        activityRepo.save(activity);
        userRepo.save(user);
        return "Update successfully";
    }
}