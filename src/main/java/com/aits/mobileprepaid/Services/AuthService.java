package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.LoginRequestDTO;
import com.aits.mobileprepaid.DTOs.LoginResponseDTO;
import com.aits.mobileprepaid.DTOs.RegisterRequestDTO;
import com.aits.mobileprepaid.DTOs.RegisterResponseDTO;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Entities.User.Role;
import com.aits.mobileprepaid.Repositories.UserRepository;
import com.aits.mobileprepaid.Security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getMobile(), loginRequestDTO.getPassword())
        );

        User user= (User) authentication.getPrincipal();
        String token = jwtUtil.generateJWT(user);


        return new LoginResponseDTO(token,user.getName(),user.getMobile(),user.getEmail());

    }

    public RegisterResponseDTO signUp(RegisterRequestDTO registerRequestDTO) {

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setMobile(registerRequestDTO.getMobile());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(registerRequestDTO.getRole());

        userRepository.save(user);

        return new RegisterResponseDTO(user.getName(), user.getMobile(), user.getEmail());

    }
}
