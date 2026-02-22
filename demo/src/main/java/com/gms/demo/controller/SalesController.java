package com.gms.demo.controller;

import com.gms.demo.model.Activity;
import com.gms.demo.model.SalesVehicle;
import com.gms.demo.model.User;
import com.gms.demo.repository.ActivityRepo;
import com.gms.demo.repository.SalesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

public class SalesController {

    @Autowired
    SalesRepo salesRepo;

    @Autowired
    ActivityRepo activityRepo;

    @PostMapping("/api/admin/sales")
    public String addSaleVehicle(@RequestBody SalesVehicle vehicle) {
        salesRepo.save(vehicle);
        return "Sale vehicle added successfully";
    }

    @GetMapping("/api/admin/sales")
    public List<SalesVehicle> getAllSales() {
        return salesRepo.findAll();
    }


    @PutMapping("/api/admin/sales/{id}")
    public ResponseEntity<String> updateSaleVehicle(
            @PathVariable int id,
            @RequestParam int adminId,
            @RequestBody SalesVehicle updatedVehicle) {

        SalesVehicle existing = salesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (
                updatedVehicle.getBrand() == null ||
                updatedVehicle.getBrand().isBlank() ||
                updatedVehicle.getModel() == null ||
                updatedVehicle.getModel().isBlank()
        ) {
            return ResponseEntity.badRequest()
                    .body("Don't keep empty field and don't make changes");
        }

        if (
                existing.getVehicleType().equals(updatedVehicle.getVehicleType()) &&
                existing.getBrand().equals(updatedVehicle.getBrand()) &&
                existing.getModel().equals(updatedVehicle.getModel()) &&
                existing.getManufactureYear() == updatedVehicle.getManufactureYear() &&
                existing.getFuelType().equals(updatedVehicle.getFuelType()) &&
                existing.getPhone().equals(updatedVehicle.getPhone())
        ) {
            return ResponseEntity.badRequest().body("No changes detected");
        }


        existing.setVehicleType(updatedVehicle.getVehicleType());
        existing.setBrand(updatedVehicle.getBrand());
        existing.setModel(updatedVehicle.getModel());
        existing.setManufactureYear(updatedVehicle.getManufactureYear());
        existing.setFuelType(updatedVehicle.getFuelType());
        existing.setPhone(updatedVehicle.getPhone());


        salesRepo.save(existing);

        return ResponseEntity.ok("Vehicle updated successfully");
    }

    @DeleteMapping("/api/admin/sales/{id}")
    public ResponseEntity<String> deleteSaleVehicle(
            @PathVariable int id,
            @RequestParam int adminId) {



        SalesVehicle sv = salesRepo.findById(id).orElseThrow(()->new RuntimeException("Vehicle not found"));
        Activity objAdmin = new Activity();
        objAdmin.setUserId(adminId);
        objAdmin.setDescription("Removed the Sales vehicle '"+sv.getVehicleType()+" >> "+sv.getBrand()+"'");
        objAdmin.setRole("CUSTOMER");
        activityRepo.save(objAdmin);
        salesRepo.deleteById(id);

        return ResponseEntity.ok("Vehicle deleted successfully");
    }
}
