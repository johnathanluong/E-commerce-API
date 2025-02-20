package io.johnathanluong.ecommerce.api;

import io.johnathanluong.ecommerce.api.entity.User;
import io.johnathanluong.ecommerce.api.exception.UsernameAlreadyExistsException;
import io.johnathanluong.ecommerce.api.exception.EmailAlreadyExistsException;
import io.johnathanluong.ecommerce.api.repository.UserRepository;
import io.johnathanluong.ecommerce.api.service.UserServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator validator;

    @Mock
    private PasswordEncoder passwordEncoder; 

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "password", "test@example.com");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createUser_Successful() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(validator.validate(any(User.class))).thenReturn(Set.of());
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        User createdUser = userService.createUser(new User("newUser", "newPass", "new@email.com"));

        assertNotNull(createdUser);
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals("testUser", createdUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void createUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        User newUser = new User("existingUser", "password", "new@email.com");

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(newUser));

        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        User newUser = new User("newUser", "password", "existing@email.com");

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(newUser));

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ValidationFails() {
        when(validator.validate(any(User.class))).thenReturn(Set.of(mock(ConstraintViolation.class.asSubclass(ConstraintViolation.class))));

        User invalidUser = new User("us", "", "invalid-email");

        assertThrows(ValidationException.class, () -> userService.createUser(invalidUser));

        verify(validator, times(1)).validate(any(User.class));
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void getUserByEmail_UserFound() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserByEmail(testUser.getEmail());

        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
    }

    @Test
    void getUserByEmail_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        User foundUser = userService.getUserByEmail("nonexistent@email.com");

        assertNull(foundUser);
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void getUserById_UserFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserById(testUser.getId());

        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        verify(userRepository, times(1)).findById(testUser.getId());
    }

    @Test
    void getUserById_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        User foundUser = userService.getUserById(999L);

        assertNull(foundUser);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserByUsername_UserFound() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserByUsername(testUser.getUsername());

        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }

    @Test
    void getUserByUsername_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        User foundUser = userService.getUserByUsername("nonexistentUser");

        assertNull(foundUser);
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void updateUser_UserFound_SuccessfulUpdate() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        User existingUser = new User("oldUser", "oldPass", "old@email.com");
        existingUser.setId(testUser.getId());
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> { // Use thenAnswer
            return invocation.getArgument(0); // Return modified user
        });
        when(validator.validate(any(User.class))).thenReturn(Set.of());

        User updatedDetails = new User("newUser", "newPassword", "new@email.com");
        User updatedUserResult = userService.updateUser(testUser.getId(), updatedDetails);

        assertNotNull(updatedUserResult);
        assertEquals(testUser.getId(), updatedUserResult.getId());
        assertEquals("newUser", updatedUserResult.getUsername());
        assertEquals("hashedPassword", updatedUserResult.getPassword());
        assertEquals("new@email.com", updatedUserResult.getEmail());
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(validator, times(1)).validate(any(User.class));
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        User updatedDetails = new User("newUser", "newPass", "new@email.com");
        User updatedUserResult = userService.updateUser(999L, updatedDetails);

        assertNull(updatedUserResult);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
        verify(validator, never()).validate(any(User.class));
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser_ValidationFails() {
        User existingUser = new User("oldUser", "oldPass", "old@email.com");
        existingUser.setId(testUser.getId());
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(existingUser));
        when(validator.validate(any(User.class))).thenReturn(Set.of(mock(ConstraintViolation.class.asSubclass(ConstraintViolation.class))));

        User updatedDetails = new User("us", "", "invalid-email");

        assertThrows(ValidationException.class, () -> userService.updateUser(testUser.getId(), updatedDetails));

        verify(validator, times(1)).validate(any(User.class));
        verify(userRepository, never()).save(any(User.class));
        Mockito.verifyNoInteractions(passwordEncoder);
    }


    @Test
    void deleteUser_UserExists_SuccessfulDeletion() {
        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        boolean deletionResult = userService.deleteUser(testUser.getId());

        assertTrue(deletionResult);
        verify(userRepository, times(1)).existsById(testUser.getId());
        verify(userRepository, times(1)).deleteById(testUser.getId());
    }

    @Test
    void deleteUser_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        boolean deletionResult = userService.deleteUser(999L);

        assertFalse(deletionResult);
        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
    }
}