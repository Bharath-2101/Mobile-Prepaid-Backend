package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.User.*;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    private String mobile;
    private String email;
    private String password;
    private Role role;
}
