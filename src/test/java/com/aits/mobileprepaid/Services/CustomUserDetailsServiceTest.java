package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    User testUser=new User(1L,"User","9999999999","User@gmail.com","User12345", User.Role.ADMIN);

    //Positive Case: Gets the User with mobile number.
    @Test
    void itShouldHaveToGiveUserWithGivenMobileNumber(){
        //Given
        String mobile="9999999999";
        //Mock
        when(userRepository.findByMobile(mobile)).thenReturn(Optional.of(testUser));
        //Act
        UserDetails user=customUserDetailsService.loadUserByUsername(mobile);
        //Assert
        assertNotNull(user);
        assertEquals(testUser,user);
    }

    //Negative Case:Throws Exception user with mobile number does not exist.
    @Test
    void itShouldHaveThrowExceptionWhenUserNotFound(){
        //Given
        String mobile="9999999999";
        //Mock
        when(userRepository.findByMobile(mobile)).thenReturn(Optional.empty());
        //Assert
        assertThrows(UsernameNotFoundException.class,() -> customUserDetailsService.loadUserByUsername(mobile));
    }

}