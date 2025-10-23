package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.RegisterRequestDTO;
import com.aits.mobileprepaid.DTOs.RegisterResponseDTO;
import com.aits.mobileprepaid.DTOs.UserRequestDTO;
import com.aits.mobileprepaid.DTOs.UserResponseDTO;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Exceptions.AlreadyExistsException;
import com.aits.mobileprepaid.Exceptions.IllegalUpdateException;
import com.aits.mobileprepaid.Repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(user.getId(),
                        user.getName(),
                        user.getMobile(),
                        user.getEmail(),
                        user.getRole()))
                .collect(Collectors.toList());
    }


    public UserResponseDTO getUserByMobile(String mobile) {

        User user=userRepository.findByMobile(mobile).orElseThrow(()->new UsernameNotFoundException("User not found with mobile "+mobile));

        return new UserResponseDTO(user.getId(),user.getName(),user.getMobile(),user.getEmail(),user.getRole());
    }


    public UserResponseDTO updateUsers(long id, UserRequestDTO user, Authentication authentication) throws UsernameNotFoundException, IllegalUpdateException {

        User unUpdatedUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + id));

        User currentUser = (User) authentication.getPrincipal();

        if (!currentUser.getMobile().equals(unUpdatedUser.getMobile()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalUpdateException("Updating is not allowed.");
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            unUpdatedUser.setName(user.getName());
        }

        if (user.getMobile() != null && !user.getMobile().isBlank()) {
            unUpdatedUser.setMobile(user.getMobile());
        }

            if (currentUser.getRole() == User.Role.ADMIN)
            {
                if (user.getRole()!=null && !user.getRole().toString().isBlank())
                {
                    unUpdatedUser.setRole(user.getRole());
                }
            }
            else {
                if (user.getRole()!=null && !user.getRole().toString().isBlank() && user.getRole() != User.Role.ADMIN)
                {
                    unUpdatedUser.setRole(user.getRole());
                }
                else {
                    throw new IllegalUpdateException("Role is not allowed.");
                }
            }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            unUpdatedUser.setPassword(encodedPassword);
        }

        User SavedUser=userRepository.save(unUpdatedUser);

        return new UserResponseDTO(SavedUser.getId(),SavedUser.getName(),SavedUser.getMobile(),SavedUser.getEmail(),SavedUser.getRole());
    }





    public String deleteUser(long id, Authentication authentication) throws IllegalUpdateException {
        User currentUser = (User) authentication.getPrincipal();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + id));

        if (!currentUser.getMobile().equals(user.getMobile()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalUpdateException("You are not authorized to delete this user.");
        }

        userRepository.delete(user);

        return "User deleted successfully with id " + id;
    }

    public RegisterResponseDTO addAdmin(@Valid RegisterRequestDTO registerRequestDTO) {
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

    public String updateUserIntoAdmin(Long id) {

        User user = userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User not found with this id"+id));

        if(user.getRole().equals(User.Role.ADMIN))
        {
            return "No Change is made the given user of id "+id+" is already a ADMIN";
        }

        user.setRole(User.Role.ADMIN);

        userRepository.save(user);

        return "Changed the role of User with id "+id+" to ADMIN";


    }
}
