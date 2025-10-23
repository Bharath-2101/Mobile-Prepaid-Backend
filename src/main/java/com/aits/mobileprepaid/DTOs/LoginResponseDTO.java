package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.User.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;

    private String name;

    private String mobile;

    private String email;

    private Role role;



}
