package com.aits.mobileprepaid.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponseDTO {

    private String name;
    private String email;
    private String mobile;

}
