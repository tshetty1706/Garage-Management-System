package com.gms.demo.controller;

import com.gms.demo.model.Activity;
import com.gms.demo.repository.ActivityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

public class ActivityController {
    @Autowired
    ActivityRepo activityRepo;

    @GetMapping("/api/activities/user/{userId}")
    public List<Activity> activityList(@PathVariable int userId)
    {
        return activityRepo.findByUserIdOrderByDateDesc(userId);
    }
}
