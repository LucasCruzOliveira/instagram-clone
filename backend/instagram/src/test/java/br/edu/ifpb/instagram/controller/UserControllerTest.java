package br.edu.ifpb.instagram.controller;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnListOfUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "Lucas Cruz", "lucas", "lucas@email.com", null, null),
                new UserDto(2L, "Maria Silva", "maria", "maria@email.com", null, null)
        );

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Lucas Cruz"))
                .andExpect(jsonPath("$[1].username").value("maria"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        UserDto user = new UserDto(1L, "Lucas Cruz", "lucas", "lucas@email.com", null, null);

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("lucas"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDetailsRequest request = new UserDetailsRequest(
                1L,
                "Lucas Atualizado",
                "lucas",
                "lucas@email.com",
                "123456"
        );

        UserDto updatedUser = new UserDto(
                1L,
                "Lucas Atualizado",
                "lucas",
                "lucas@email.com",
                null,
                null
        );

        when(userService.updateUser(any())).thenReturn(updatedUser);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Lucas Atualizado"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("user was deleted!"));
    }
}
