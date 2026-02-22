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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        User existing = userRepo.findByRole("SUPER_ADMIN");
        if (existing == null) {
            User superAdmin = new User();
            superAdmin.setName("Vijay S Shetty");
            superAdmin.setUsername("Vshetty1974");
            superAdmin.setPassword("1974");
            superAdmin.setEmail("vshetty1974@gmail.com");
            superAdmin.setRole("SUPER_ADMIN");


            Activity obj = new Activity();
            obj.setUserId(superAdmin.getId());
            obj.setDescription("Welcome "+superAdmin.getUsername()+" to GMS! Your account has been created successfully.");
            obj.setRole(superAdmin.getRole());
            activityRepo.save(obj);
            userRepo.save(superAdmin);
        }

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

    @GetMapping("/api/admin/dashboard/{adminId}")
    public DashboardResponse getDashboard(@PathVariable int adminId) {

        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        DashboardResponse response = new DashboardResponse();

        // ADMIN INFO
        response.setName(admin.getName());
        response.setUserName(admin.getUsername());

        // STATS
        response.setTotalUsers(userRepo.countByRole("CUSTOMER"));
        response.setActiveServices(bookingRepo.countByStatus("ACTIVE"));
        response.setPendingRequests(bookingRepo.countByStatus("PENDING"));
        response.setTotalRevenue(bookingRepo.getTotalRevenue());

        List<ServiceBooking> bookings =
                bookingRepo.findTop5ByOrderByBookDateDesc();

        List<RecentActivityDto> activityList = new ArrayList<>();

        for (ServiceBooking booking : bookings) {

            RecentActivityDto dto = new RecentActivityDto();

            dto.setDate(booking.getBookDate().toString());
            dto.setBookingId(String.valueOf(booking.getBookingId()));
            dto.setStatus(booking.getStatus());

            activityList.add(dto);
        }

        response.setRecentActivity(activityList);

        return response;
    }


    @GetMapping("/api/admin/users")
    public List<UserListDto> getUsers(
            @RequestParam(defaultValue = "ASC") String sort,
            @RequestParam(required = false) String search
    ) {

        Sort.Direction direction =
                sort.equalsIgnoreCase("DESC")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Sort sortObj = Sort.by(direction, "name");

        List<User> users;

        if (search != null && !search.isEmpty()) {

            users = userRepo
                    .findByUsernameContainingIgnoreCaseAndRole(
                            search,
                            "CUSTOMER",
                            sortObj
                    );

        } else {

            users = userRepo.findByRole("CUSTOMER", sortObj);
        }

        List<UserListDto> result = new ArrayList<>();

        for (User user : users) {

            boolean hasActive =
                    bookingRepo.existsByUserId(
                            user.getId()
                    );

            boolean hasPending =
                    bookingRepo.existsByUserIdAndStatus(
                            user.getId(),
                            "PENDING"
                    );

            String status =
                    hasActive ? "Active" : "Not Active";

            boolean canDelete =
                    !(hasActive || hasPending);

            UserListDto dto = new UserListDto(
                    user.getId(),
                    user.getName(),
                    user.getUsername(),
                    user.getEmail(),
                    status,
                    canDelete
            );

            result.add(dto);
        }

        return result;
    }

    @DeleteMapping("/api/admin/user/{id}")
    public String deleteUser(@PathVariable int id,@RequestParam int adminId) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasActive =
                bookingRepo.existsByUserIdAndStatus(id, "ACTIVE");

        boolean hasPending =
                bookingRepo.existsByUserIdAndStatus(id, "PENDING");

        User user1 = userRepo.findById(adminId).orElseThrow(()-> new RuntimeException("Admin Not found"));
        Activity obj = new Activity();
        obj.setUserId(user1.getId());
        obj.setRole(user1.getRole());

        if (hasActive || hasPending) {
            obj.setDescription("Deletion of '"+user.getUsername()+"' was unsuccessful");
            activityRepo.save(obj);
            throw new RuntimeException(
                    "User has active or pending services"
            );
        }

        List<Vehicle> vehicles = vehicleRepo.findAllByUserId(user.getId());
        vehicleRepo.deleteAll(vehicles);


        obj.setDescription("Deleted user '" + user.getUsername() + "' ");
        activityRepo.save(obj);
        userRepo.delete(user);
        return "User deleted successfully";
    }

    @GetMapping("/api/admin/user/{id}/vehicles")
    public List<Vehicle> getUserVehicles(@PathVariable int id) {

        return vehicleRepo.findAllByUserId(id);
    }

    @PostMapping("/api/admin/user")
    public ResponseEntity<String> addUser(@RequestBody User user,@RequestParam int adminId) {

        if(userRepo.findByUsername(user.getUsername()) != null)
        {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if(userRepo.findByEmail(user.getEmail()) !=null)
        {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        if (!user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return ResponseEntity.badRequest().body("Invalid email format!");
        }

        user.setRole("CUSTOMER");
        userRepo.save(user);

        User admin = userRepo.findById(adminId).orElseThrow(()->new RuntimeException("Admin not found"));
        Activity adminObj = new Activity();
        Activity userObj = new Activity();

        adminObj.setUserId(admin.getId());
        adminObj.setRole(admin.getRole());
        adminObj.setDescription("Created user : '"+user.getUsername()+"' ");

        userObj.setUserId(user.getId());
        userObj.setDescription("Your account has been successfully created by an administrator. "+admin.getUsername()+"You can now log in and start using Garage Management System services ");
        userObj.setRole(user.getRole());

        activityRepo.save(adminObj);
        activityRepo.save(userObj);

        return ResponseEntity.ok("User added successfully");
    }

    @PostMapping("/api/admin/admin")
    public ResponseEntity<String> addAdmin(@RequestBody User user,@RequestParam int adminId) {

        if(userRepo.findByUsername(user.getUsername()) != null)
        {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if(userRepo.findByEmail(user.getEmail()) !=null)
        {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        if (!user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return ResponseEntity.badRequest().body("Invalid email format!");
        }

        user.setRole("ADMIN");
        userRepo.save(user);

        User admin = userRepo.findById(adminId).orElseThrow(()->new RuntimeException("Admin not found"));
        Activity adminObj = new Activity();
        Activity newAdminObj = new Activity();

        adminObj.setUserId(admin.getId());
        adminObj.setRole(admin.getRole());
        adminObj.setDescription("Created new admin : '"+user.getUsername()+"' ");

        newAdminObj.setUserId(user.getId());
        newAdminObj.setDescription("Your account has been successfully created by an administrator. "+admin.getUsername()+"' ");
        newAdminObj.setRole(user.getRole());

        activityRepo.save(adminObj);
        activityRepo.save(newAdminObj);

        return ResponseEntity.ok("Admin added successfully");
    }

    @PutMapping("/api/admin/profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id,
                                                @RequestBody Map<String,String> data) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        String newName = data.get("name");
        String newEmail = data.get("email");
        String newPassword = data.get("password");


        boolean isSame =
                user.getName().equals(newName) &&
                        user.getEmail().equals(newEmail) &&
                        (newPassword == null || newPassword.isBlank());

        if (isSame) {
            return ResponseEntity
                    .badRequest()
                    .body("Provide new data. It is same as already existing one.");
        }


        if (!user.getEmail().equals(newEmail) &&
                userRepo.existsByEmail(newEmail)) {

            return ResponseEntity
                    .badRequest()
                    .body("Email already exists");
        }

        user.setName(newName);
        user.setEmail(newEmail);

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword);
        }

        Activity adminObj = new Activity();
        adminObj.setUserId(user.getId());
        adminObj.setRole(user.getRole());
        adminObj.setDescription("Updated profile successfully!");
        activityRepo.save(adminObj);

        userRepo.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/api/admin/admins")
    public List<User> getAllAdmins() {
        return userRepo.findAllByRole("ADMIN");
    }

    @DeleteMapping("/api/admin/admin/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable int id,
                                              @RequestParam int adminId) {

        if (id == adminId) {
            return ResponseEntity.badRequest()
                    .body("You cannot delete your own account");
        }

        User admin = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!admin.getRole().equals("ADMIN")) {
            return ResponseEntity.badRequest()
                    .body("Only admin accounts can be deleted");
        }

        User admin1 = userRepo.findById(adminId).orElseThrow(()->new RuntimeException("Admin not found"));
        Activity obj = new Activity();
        obj.setUserId(admin1.getId());
        obj.setRole(admin1.getRole());
        obj.setDescription("Deleted admin '" + admin.getUsername() + "' ");
        activityRepo.save(obj);

        userRepo.delete(admin);

        return ResponseEntity.ok("Admin deleted successfully");
    }

    @GetMapping("/api/admin/profile/{id}")
    public User getProfile(@PathVariable int id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }
}