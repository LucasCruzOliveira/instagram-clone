package br.edu.ifpb.instagram.controller;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.service.UserService;
import br.edu.ifpb.instagram.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private AuthServiceImpl authService;

    @MockitoBean
    private UserService userService;

    // ================= SIGNUP =================

    @Test
    void shouldSignUpUserSuccessfully() throws Exception {

        when(userService.createUser(any()))
                .thenReturn(mockUserDto());

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());
    }

    // ================= SIGNIN =================

    @Test
    void shouldSignInSuccessfully() throws Exception {

        when(authService.authenticate(any()))
                .thenReturn("fake-jwt-token");

        var request = new HashMap<String, Object>();
        request.put("username", "admin");
        request.put("password", "admin");

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ================= AUX =================

    private HashMap<String, Object> validRequest() {
        var request = new HashMap<String, Object>();
        request.put("fullName", "Lucas");
        request.put("username", "lucas_123");
        request.put("email", "lucas@email.com");
        request.put("password", "123456");
        return request;
    }

    private UserDto mockUserDto() {
        return new UserDto(
                1L,
                "Lucas",
                "lucas_123",
                "lucas@email.com",
                "123456",
                null
        );
    }
}
