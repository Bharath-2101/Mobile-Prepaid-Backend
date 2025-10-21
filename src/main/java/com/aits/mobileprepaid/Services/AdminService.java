package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.RegisterRequestDTO;
import com.aits.mobileprepaid.DTOs.RegisterResponseDTO;
import com.aits.mobileprepaid.Entities.User.*;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Exceptions.AlreadyExistsException;
import com.aits.mobileprepaid.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponseDTO addAdmin(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.findByMobile(registerRequestDTO.getMobile()).isPresent()) {
            throw new AlreadyExistsException("User already exists with mobile "+registerRequestDTO.getMobile());
        }

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setMobile(registerRequestDTO.getMobile());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(registerRequestDTO.getRole());
        userRepository.save(user);

        return new RegisterResponseDTO(user.getName(),user.getEmail(),user.getMobile());
    }

    public String updateUserIntoAdmin(Long id)
    {
        User user = userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User not found with this id"+id));

        if(user.getRole().equals(Role.ADMIN))
        {
            return "No Change is made the given user of id "+id+" is already a ADMIN";
        }

        user.setRole(Role.ADMIN);

        userRepository.save(user);

        return "Changed the role of User with id "+id+" to ADMIN";

    }

}
