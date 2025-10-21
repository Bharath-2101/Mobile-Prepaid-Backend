package com.aits.mobileprepaid.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.aits.mobileprepaid.DTOs.UserRequestDTO;
import com.aits.mobileprepaid.DTOs.UserResponseDTO;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Entities.User.Role;
import com.aits.mobileprepaid.Exceptions.IllegalUpdateException;
import com.aits.mobileprepaid.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    private User updatedUser;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Alice", "1234567890", "alice@example.com","User12345", Role.USER);
        updatedUser = new User(2L, "Bob", "9876543210", "bob@example.com","User22345" ,Role.ADMIN);
    }

    //Positive Case: Get All the Users
    @Test
    void testGetAllUsersReturnsUserResponseDTOList() {

        // Arrange - Create sample users
        User user1 = new User(1L, "Alice", "1234567890", "alice@example.com", "User12345", Role.USER);
        User user2 = new User(2L, "Bob", "9876543210", "bob@example.com","User22345" ,Role.ADMIN);

        // Mock userRepository.findAll() to return sample users
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());

        UserResponseDTO dto1 = result.get(0);
        assertEquals(user1.getId(), dto1.getId());
        assertEquals(user1.getName(), dto1.getName());
        assertEquals(user1.getMobile(), dto1.getMobile());
        assertEquals(user1.getEmail(), dto1.getEmail());
        assertEquals(user1.getRole(), dto1.getRole());

        UserResponseDTO dto2 = result.get(1);
        assertEquals(user2.getId(), dto2.getId());
        assertEquals(user2.getName(), dto2.getName());
        assertEquals(user2.getMobile(), dto2.getMobile());
        assertEquals(user2.getEmail(), dto2.getEmail());
        assertEquals(user2.getRole(), dto2.getRole());

        // Verify
        verify(userRepository).findAll();
    }

    //Positive Case: Get User By Mobile
    @Test
    void itShouldReturnUserIfGivenMobileAlreadyExists() {
        //Given


        //Mock
        when(userRepository.findByMobile(user.getMobile())).thenReturn(Optional.of(user));

        //Act
        UserResponseDTO actualResult = userService.getUserByMobile(user.getMobile());

        //Assert
        assertNotNull(actualResult);
        assertEquals(user.getId(), actualResult.getId());
        assertEquals(user.getName(), actualResult.getName());
        assertEquals(user.getEmail(), actualResult.getEmail());
        assertEquals(user.getRole(), actualResult.getRole());
        assertEquals(user.getMobile(), actualResult.getMobile());

        verify(userRepository).findByMobile(user.getMobile());

    }

    //Negative Case: Throws Exception when the Mobile does not exist.
    @Test
    void itShouldThrowExceptionIfUserWithGivenMobileDoesNotExist() {

        //Mock
        when(userRepository.findByMobile(user.getMobile())).thenThrow(new UsernameNotFoundException("User not found with given mobile"+user.getMobile()));

        //assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByMobile(user.getMobile()));
    }

    //Positive Test Case: Update User with given updates without change or By setting Role as USER
    @Test
    void itShouldUpdateUserIfCurrentUserHasRoleOfUserWithNoChangeInRoleOrSameRole() {
        long id = 1L;

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                updatedUser.getName(),
                updatedUser.getMobile(),
                updatedUser.getEmail(),
                updatedUser.getPassword(),
                Role.USER
        );

        User UpdatedUser=new User(id,
                userRequestDTO.getName(),
                userRequestDTO.getMobile(),
                userRequestDTO.getEmail(),
                userRequestDTO.getPassword(),
                userRequestDTO.getRole());

        Authentication authentication = mock(Authentication.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(authentication.getPrincipal()).thenReturn(user);
        when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("Encoded-Password");

        when(userRepository.save(any(User.class))).thenReturn(UpdatedUser);

        UserResponseDTO actualResult = userService.updateUsers(id, userRequestDTO, authentication);

        assertNotNull(actualResult);
        assertEquals(id, actualResult.getId());
        assertEquals(userRequestDTO.getName(), actualResult.getName());
        assertEquals(userRequestDTO.getEmail(), actualResult.getEmail());
        assertEquals(userRequestDTO.getMobile(), actualResult.getMobile());
        assertEquals(userRequestDTO.getRole(), actualResult.getRole());
    }


    //Negative Test Case:Throws Exception if User does not exist with given id.
    @Test
    void itShouldThrowExceptionIfUserWithGivenIdDoesNotExist() {
        // Given
        long id = 1L;
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        Authentication authentication = mock(Authentication.class);

        // Mock
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userService.updateUsers(id, userRequestDTO, authentication));
    }

    //Negative Test Case: Throws Exception if the person who tries to update the User is neither Owner nor ADMIN.
    @Test
    void itShouldThrowExceptionIfUserWithGivenIdIsNeitherOwnerNorAdminTriesToUpdate()
    {
        //Given
        long id = 1L;
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        Authentication authentication = mock(Authentication.class);

        //Mock
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(authentication.getPrincipal()).thenReturn(user);

        //Assert
        assertThrows(IllegalUpdateException.class, () -> userService.updateUsers(id, userRequestDTO, authentication));
    }



    //Positive Case:Delete the User if the Mobile and the user is owner
    @Test
    void itShouldDeleteTheUserWithGivenIdIfUserISOwner()
    {
        //Given
        long id = 2L;
        Authentication auth = mock(Authentication.class);

        //Mock
        User currentUser=new User(2L, "Bob", "9876543210", "bob@example.com","User22345" ,Role.USER);
        when(auth.getPrincipal()).thenReturn(currentUser);
        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));

        //Act
        String actualResult=userService.deleteUser(id,auth);

        //Assert
        assertEquals("User deleted successfully with id " + id, actualResult);
    }

    //Positive Test Case: Delete the User If the User is Admin and not an owner
    @Test
    void itShouldDeleteUserWithGivenIdIfDeletingUserIsAdmin()
    {
        //Given
        long id=1L;
        Authentication auth = mock(Authentication.class);

        //Mock
        User admin = new User(2L, "Bob", "9876543210", "bob@example.com","User22345" ,Role.ADMIN);
        when(auth.getPrincipal()).thenReturn(admin);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //Act
        String actualResult=userService.deleteUser(id,auth);

        //Assert
        assertEquals("User deleted successfully with id " + id, actualResult);
    }

    //Negative Test Case:Throws Exception if No User exist with given id.
    @Test
    void itShouldThrowExceptionIfUserWithGivenIdIsNotExists()
    {
        //Given
        long id = 2L;
        Authentication auth = mock(Authentication.class);

        //Mock
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser(id,auth));
    }

    //Negative Test Case: Throws Exception if the person who tries to delete the User is neither Owner nor ADMIN.
    @Test
    void itShouldThrowExceptionIfUserWithGivenIdIsNeitherOwnerNorAdmin()
    {
        //Given
        long id = 1L;
        Authentication auth = mock(Authentication.class);


        //Mock
        User testUser = new User(2L, "Bob", "9876543210", "bob@example.com","User22345" ,Role.USER);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(auth.getPrincipal()).thenReturn(testUser);

        //Assert
        assertThrows(IllegalUpdateException.class, () -> userService.deleteUser(id,auth));
    }

}
