package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.LoginRequestDTO;
import com.aits.mobileprepaid.DTOs.LoginResponseDTO;
import com.aits.mobileprepaid.DTOs.RegisterRequestDTO;
import com.aits.mobileprepaid.DTOs.RegisterResponseDTO;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Exceptions.AlreadyExistsException;
import com.aits.mobileprepaid.Repositories.UserRepository;
import com.aits.mobileprepaid.Security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequestDTO loginRequestDTO;
    private RegisterRequestDTO registerRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User(1L, "John", "9999999999", "John@example.in", "John12345", User.Role.USER);

        registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail(testUser.getEmail());
        registerRequestDTO.setPassword(testUser.getPassword());
        registerRequestDTO.setMobile(testUser.getMobile());
        registerRequestDTO.setName(testUser.getName());
        registerRequestDTO.setRole(testUser.getRole());

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setMobile(testUser.getMobile());
        loginRequestDTO.setPassword(testUser.getPassword());
    }

    //Positive Case:Login Successful.
    @Test
    void itShouldGiveValidLoginResponseDTOIfValidAndCorrectLoginRequestDTOIsGiven() {
        // Mock Authentication and JWT
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateJWT(testUser)).thenReturn("MockedJWTToken");

        // Act
        LoginResponseDTO response = authService.login(loginRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("MockedJWTToken", response.getToken());
        assertEquals("John", response.getName());
        assertEquals("9999999999", response.getMobile());
        assertEquals("John@example.in", response.getEmail());
    }


    //Negative Case:Login with Invalid Credentials.
    @Test
    void itShouldThrowExceptionIfInvalidLoginRequestDTOIsGiven() {
        //Act
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad Credentials"));

        //Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDTO));
    }


    //Positive Case: Register Successful.
    @Test
    void itShouldGiveRegisterResponseDTOIfTheRegistrationIsSuccessful() {

        //Mock
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("Encoded-Password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        //Act
        RegisterResponseDTO actualResult = authService.signUp(registerRequestDTO);
        RegisterResponseDTO expectedResult = new RegisterResponseDTO("John", "John@example.in", "9999999999");

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    //Negative Case: Register with Already exists mobile.
    @Test
    void itShouldThrowExceptionIfMobileIsNotUnique() {
        when(userRepository.findByMobile(registerRequestDTO.getMobile()))
                .thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () -> authService.signUp(registerRequestDTO));
    }

}
