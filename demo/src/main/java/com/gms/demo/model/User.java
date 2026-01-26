package com.gms.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false,unique = true)
    String username;

    @Column(nullable = false,unique = true)
    String email;

    String phone;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String role;
}


//user table(customer + admin)
//signin will ask for (Full Name,Username,Email,Password)
//login will ask for (username,password) -> role == customer -> user_dashboard.html
//                                       -> role == admin -> admin.html
//