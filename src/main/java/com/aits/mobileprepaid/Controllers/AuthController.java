package com.aits.mobileprepaid.Controllers;

import com.aits.mobileprepaid.DTOs.LoginRequestDTO;
import com.aits.mobileprepaid.DTOs.LoginResponseDTO;
import com.aits.mobileprepaid.DTOs.RegisterRequestDTO;
import com.aits.mobileprepaid.DTOs.RegisterResponseDTO;
import com.aits.mobileprepaid.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO)
    {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> signUp(@Valid  @RequestBody RegisterRequestDTO registerRequestDTO)
    {
        return ResponseEntity.ok(authService.signUp(registerRequestDTO));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> PingAlive()
    {
        return ResponseEntity.ok("The Backend is alive.");
    }

}
