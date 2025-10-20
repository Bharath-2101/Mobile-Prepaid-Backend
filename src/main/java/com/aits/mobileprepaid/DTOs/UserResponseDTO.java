package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.User.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String name;
    private String mobile;
    private String email;
    private Role role;
}
