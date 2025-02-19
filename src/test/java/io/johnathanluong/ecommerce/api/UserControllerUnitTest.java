package io.johnathanluong.ecommerce.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.johnathanluong.ecommerce.api.controller.UserControllerImpl;
import io.johnathanluong.ecommerce.api.entity.User;
import io.johnathanluong.ecommerce.api.exception.EmailAlreadyExistsException;
import io.johnathanluong.ecommerce.api.exception.UsernameAlreadyExistsException;
import io.johnathanluong.ecommerce.api.service.UserService;
import jakarta.validation.ValidationException;

@WebMvcTest(value = UserControllerImpl.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_Successful() throws Exception {
        User newUser = new User("newUser", "password", "new@email.com");
        User createdUser = new User("newUser", "password", "new@email.com");
        createdUser.setId(1L);

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/users/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@email.com"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists() throws Exception {
        User newUser = new User("existingUser", "password", "new@email.com");
        when(userService.createUser(any(User.class))).thenThrow(new UsernameAlreadyExistsException("Username already in use."));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists() throws Exception {
        User newUser = new User("newUser", "password", "existing@email.com");
        when(userService.createUser(any(User.class))).thenThrow(new EmailAlreadyExistsException("Email already in use."));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_ValidationFails() throws Exception {
        User invalidUser = new User("us", "", "invalid-email");
        when(userService.createUser(any(User.class))).thenThrow(new ValidationException("User validation failed"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void getUserByEmail_UserFound() throws Exception {
        User testUser = new User("testUser", "password", "test@example.com");
        when(userService.getUserByEmail(testUser.getEmail())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/email/{email}", testUser.getEmail())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserByEmail(anyString());
    }

    @Test
    void getUserByEmail_UserNotFound() throws Exception {
        when(userService.getUserByEmail(anyString())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/email/{email}", "nonexistent@email.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByEmail(anyString());
    }

    @Test
    void getUserById_UserFound() throws Exception {
        User testUser = new User("testUser", "password", "test@example.com");
        testUser.setId(1L);
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", testUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));


        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    void getUserById_UserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", 999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    void getUserByUsername_UserFound() throws Exception {
        User testUser = new User("testUser", "password", "test@example.com");
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/{username}", testUser.getUsername())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserByUsername(anyString());
    }

    @Test
    void getUserByUsername_UserNotFound() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/{username}", "nonexistentUser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByUsername(anyString());
    }

    @Test
    void updateUser_UserFound_SuccessfulUpdate() throws Exception {
        Long userId = 1L;
        User updatedDetails = new User("newUser", "newPassword", "new@email.com");
        User updatedUserResult = new User("newUser", "newPassword", "new@email.com");
        updatedUserResult.setId(userId);

        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUserResult);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@email.com"));

        verify(userService, times(1)).updateUser(anyLong(), any(User.class));
    }

    @Test
    void updateUser_UserNotFound() throws Exception {
        Long userId = 999L;
        User updatedDetails = new User("newUser", "newPassword", "new@email.com");
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(anyLong(), any(User.class));
    }

    @Test
    void updateUser_ValidationFails() throws Exception {
        Long userId = 1L;
        User invalidUser = new User("us", "", "invalid-email");
        when(userService.updateUser(anyLong(), any(User.class))).thenThrow(new ValidationException("User validation failed"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).updateUser(anyLong(), any(User.class));
    }

    @Test
    void deleteUser_UserExists_SuccessfulDeletion() throws Exception {
        Long userId = 1L;
        when(userService.deleteUser(anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    void deleteUser_UserNotFound() throws Exception {
        Long userId = 999L;
        when(userService.deleteUser(anyLong())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(anyLong());
    }
}