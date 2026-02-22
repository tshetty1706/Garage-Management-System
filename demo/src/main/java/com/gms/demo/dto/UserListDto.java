package com.gms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserListDto {
    private int id;
    private String name;
    private String username;
    private String email;
    private String status;       // Active / Not Active
    private boolean canDelete;   // true / false
}
