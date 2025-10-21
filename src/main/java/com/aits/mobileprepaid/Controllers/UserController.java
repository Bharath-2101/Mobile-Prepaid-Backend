package com.aits.mobileprepaid.Controllers;

import com.aits.mobileprepaid.DTOs.UserRequestDTO;
import com.aits.mobileprepaid.DTOs.UserResponseDTO;
import com.aits.mobileprepaid.Services.UserService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //Get all users
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>>getAllUsers() {
        return  ResponseEntity.ok(userService.getAllUsers());
    }

    //Get A user using Mobile
    @GetMapping("/{mobile}")
    public ResponseEntity<UserResponseDTO> getUserByMobile(
            @PathVariable
            @Pattern(
                    regexp = "^[6-9]\\d{9}$",
                    message = "Invalid mobile number — must be 10 digits and start with 6–9"
            )
            String mobile) {
        return ResponseEntity.ok(userService.getUserByMobile(mobile));
    }

    //Update User by id
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable long id, @RequestBody UserRequestDTO userRequestDTO, Authentication authentication) {
        return ResponseEntity.ok(userService.updateUsers(id, userRequestDTO, authentication));
    }

    //Delete User by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(userService.deleteUser(id,authentication));
    }


}
