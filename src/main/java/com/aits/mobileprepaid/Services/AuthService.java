package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.LoginRequestDTO;
import com.aits.mobileprepaid.DTOs.LoginResponseDTO;
import com.aits.mobileprepaid.DTOs.RegisterRequestDTO;
import com.aits.mobileprepaid.DTOs.RegisterResponseDTO;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Entities.User.Role;
import com.aits.mobileprepaid.Exceptions.AlreadyExistsException;
import com.aits.mobileprepaid.Repositories.UserRepository;
import com.aits.mobileprepaid.Security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getMobile(), loginRequestDTO.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateJWT(user);


            return new LoginResponseDTO(token, user.getName(), user.getMobile(), user.getEmail(),user.getRole());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid password");
        }catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User does not exist with given Mobile "+loginRequestDTO.getMobile());
        }

    }


    public RegisterResponseDTO signUp(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.findByMobile(registerRequestDTO.getMobile()).isPresent()) {
            throw new AlreadyExistsException("User already exists with mobile " + registerRequestDTO.getMobile());
        }

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setMobile(registerRequestDTO.getMobile());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return new RegisterResponseDTO(user.getName(), user.getEmail(), user.getMobile());
    }
}
