package com.example.demo.ControllerTesting;

import com.example.demo.buisnessUsage.users.structure.User;
import com.example.demo.buisnessUsage.users.structure.UserController;
import com.example.demo.buisnessUsage.users.structure.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Optional;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTesting {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_GETMethod_PositiveCase() throws Exception {
        //Arrange
        User mockUser = new User(
                "default_id",
                "default_generatedKey",
                "default_uid",
                Instant.now()
        );
        Mockito.when(userService.getUser(Mockito.any(String.class))).thenReturn(Optional.of(mockUser));

        //Act
        var result = mockMvc.perform(get("/versatile_api/users").param("uid", "default_uid"));

        //Assert
        result.andExpect(status().isFound())
                .andExpect(jsonPath("$.generatedKey").value(mockUser.getGeneratedKey()))
                .andExpect(jsonPath("$.timeCreated").exists());
    }

    @Test
    public void test_GETMethod_NegativeCase() throws Exception {
        //Arrange
        User mockUser = new User(
                "default_id",
                "default_generatedKey",
                "default_uid",
                Instant.now()
        );
        Mockito.when(userService.getUser(Mockito.any(String.class))).thenReturn(Optional.empty());

        //Act
        var result = mockMvc.perform(get("/versatile_api/users").param("uid", mockUser.getUid()));

        //Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    public void test_GETMethod_ErrorCase() {
        //Arrange
        Mockito.when(userService.getUser(Mockito.any(String.class))).thenCallRealMethod();

        //Act & Assert
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/versatile_api/users").param("uid", ""));
        });
    }


    @Test
    public void test_POSTMethod_PositiveCase() throws Exception {
        //Arrange
        User responseUser = new User(
                "default_id",
                "default_generatedKey",
                "default_uid",
                Instant.now()
        );
        Mockito.when(userService.createUser(Mockito.any(String.class))).thenReturn(responseUser);

        //Act
        var result = mockMvc.perform(post("/versatile_api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\": \"%s\"\n" +
                        "}".formatted(responseUser.getUid())));

        //Assert
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseUser.getId()))
                .andExpect(jsonPath("$.generatedKey").value(responseUser.getGeneratedKey()))
                .andExpect(jsonPath("$.uid").value(responseUser.getUid()))
                .andExpect(jsonPath("timeCreated").value(responseUser.getTimeCreated().toString()));
    }

    @Test
    public void test_POSTMethod_NegativeCase() throws Exception {
        //Arrange
        User responseUser = new User(
                "default_id",
                "default_generatedKey",
                "default_uid",
                Instant.now()
        );
        Mockito.when(userService.doesUserExist(Mockito.any(String.class))).thenReturn(true);

        //Act
        var result = mockMvc.perform(post("/versatile_api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\": \"%s\"\n" +
                        "}".formatted(responseUser.getUid())));

        //Assert
        result.andExpect(status().isFound());
    }

    @Test
    public void test_POSTMethod_ErrorCase() {
        //Arrange
        Mockito.when(userService.doesUserExist(Mockito.any(String.class))).thenCallRealMethod();

        //Act & Assert
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/versatile_api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "uid": ""
                        }"""));
        });
    }


    @Test
    public void test_DELETEMethod_PositiveCase() throws Exception {
        //Arrange
        String requestUid = "default_uid";

        //Act
        var result = mockMvc.perform(delete("/versatile_api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\": \"%s\"\n" +
                        "}".formatted(requestUid)));

        //Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void test_DELETEMethod_ErrorCase() {
        //Arrange
        Mockito.doCallRealMethod().when(userService).removeUser(Mockito.any(String.class));

        //Act & Assert
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(delete("/versatile_api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "uid": ""
                        }"""));
        });
    }
}
