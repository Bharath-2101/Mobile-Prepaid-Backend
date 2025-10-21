package com.aits.mobileprepaid.Controllers;

import com.aits.mobileprepaid.DTOs.RegisterRequestDTO;
import com.aits.mobileprepaid.DTOs.RegisterResponseDTO;
import com.aits.mobileprepaid.Services.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/user")
    public ResponseEntity<RegisterResponseDTO> addUserWithAdminRole(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        return ResponseEntity.ok(adminService.addAdmin(registerRequestDTO));

    }

    @PutMapping("/user/{id}")
    public ResponseEntity<String> updateUserToAdmin(@PathVariable Long id)
    {
        return ResponseEntity.ok(adminService.updateUserIntoAdmin(id));
    }


}
