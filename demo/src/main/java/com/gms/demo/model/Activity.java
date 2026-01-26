package com.gms.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    int userId;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    String role;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    LocalDateTime date;

}
