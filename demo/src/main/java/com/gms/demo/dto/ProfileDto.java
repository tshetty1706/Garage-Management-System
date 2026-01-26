package com.gms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.internal.build.AllowNonPortable;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProfileDto {
    String name;
    String username;
    String email;
    String phone;
    String role;
}
