package com.example.demo.ControllerTesting;

import com.example.demo.backendUsage.config.RateLimiting.UserRateLimiting.UserReadRateLimiterConfig;
import com.example.demo.backendUsage.config.RateLimiting.UserRateLimiting.UserWriteRateLimiterConfig;
import com.example.demo.buisnessUsage.users.structure.DTO.UserDTO;
import com.example.demo.buisnessUsage.users.structure.DTO.UserDTOMapper;
import com.example.demo.buisnessUsage.users.structure.User;
import com.example.demo.buisnessUsage.users.structure.UserController;
import com.example.demo.buisnessUsage.users.structure.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.google.common.util.concurrent.RateLimiter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTesting {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserReadRateLimiterConfig userReadRateLimiterConfig;

    @MockitoBean
    private UserWriteRateLimiterConfig userWriteRateLimiterConfig;

    @MockitoBean
    private UserDTOMapper userDTOMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpRateLimiters() {
        RateLimiter dummyLimiter = RateLimiter.create(100);
        Mockito.when(userReadRateLimiterConfig.getUserCustomReadRateLimiter()).thenReturn(dummyLimiter);
        Mockito.when(userWriteRateLimiterConfig.getUserCustomWriteRateLimiter()).thenReturn(dummyLimiter);
    }

    @Test
    public void test_GetAllUsers_PositiveCase() throws Exception {
        // Arrange
        Instant now = Instant.now();
        User mockUser = new User("default_id", "default_uid", now);
        UserDTO mockUserDTO = new UserDTO("default_uid", now);

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(mockUser));
        Mockito.when(userDTOMapper.apply(Mockito.any(User.class))).thenReturn(mockUserDTO);

        // Act
        var result = mockMvc.perform(get("/versatile_api/users"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uid").value("default_uid"))
                .andExpect(jsonPath("$[0].timeCreated").exists());
    }

    @Test
    public void test_GetAllUsers_EmptyCase() throws Exception {
        // Arrange
        Mockito.when(userService.getAllUsers()).thenReturn(List.of());

        // Act
        var result = mockMvc.perform(get("/versatile_api/users"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void test_POSTMethod_PositiveCase() throws Exception {
        // Arrange
        Instant now = Instant.now();
        User responseUser = new User("default_id", "default_uid", now);
        UserDTO responseUserDTO = new UserDTO("default_uid", now);

        Mockito.when(userService.createUser(Mockito.any(String.class))).thenReturn(responseUser);
        Mockito.when(userDTOMapper.apply(Mockito.any(User.class))).thenReturn(responseUserDTO);

        // Act
        var result = mockMvc.perform(post("/versatile_api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\": \"%s\"\n" +
                        "}".formatted(responseUser.getUid())));

        // Assert
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.uid").value(responseUserDTO.getUid()))
                .andExpect(jsonPath("$.timeCreated").exists());
    }

    @Test
    public void test_POSTMethod_NegativeCase_Conflict() throws Exception {
        // Arrange
        String requestUid = "default_uid";
        UserDTO fallbackDTO = new UserDTO("null", Instant.now());

        // Simulating user already exists -> userService returns null
        Mockito.when(userService.createUser(Mockito.any(String.class))).thenReturn(null);
        Mockito.when(userDTOMapper.apply(Mockito.any(User.class))).thenReturn(fallbackDTO);

        // Act
        var result = mockMvc.perform(post("/versatile_api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\": \"%s\"\n" +
                        "}".formatted(requestUid)));

        // Assert
        result.andExpect(status().isConflict());
    }

    @Test
    public void test_DELETEMethod_PositiveCase() throws Exception {
        // Arrange
        String requestUid = "default_uid";

        // Act
        var result = mockMvc.perform(delete("/versatile_api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\": \"%s\"\n" +
                        "}".formatted(requestUid)));

        // Assert
        result.andExpect(status().isOk());
    }
}