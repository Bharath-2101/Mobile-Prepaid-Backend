package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.User.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String name;
    private String mobile;
    private String email;
    private String password;
    private Role role;
}
