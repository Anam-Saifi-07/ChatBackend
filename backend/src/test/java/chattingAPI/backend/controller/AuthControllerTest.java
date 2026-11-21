package chattingAPI.backend.controller;

import chattingAPI.backend.DTO.AuthResponse;
import chattingAPI.backend.DTO.LoginRequest;
import chattingAPI.backend.DTO.RegisterRequest;
import chattingAPI.backend.Security.AuthController;
import chattingAPI.backend.Security.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_Success_Returns200AndToken() throws Exception {
        // GIVEN: Valid login credentials
        LoginRequest request = new LoginRequest();
        request.setName("JohnDoe");
        request.setPassword("password123");

        AuthResponse mockResponse = AuthResponse.builder()
                .id("u1")
                .name("JohnDoe")
                .token("fake-jwt-token-xyz")
                .mail("john@example.com")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        // WHEN & THEN: Perform POST /api/auth/login and verify response
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token-xyz"))
                .andExpect(jsonPath("$.name").value("JohnDoe"))
                .andExpect(jsonPath("$.mail").value("john@example.com"));
    }

    @Test
    void register_Success_Returns200AndToken() throws Exception {
        // GIVEN: Valid registration payload
        RegisterRequest request = RegisterRequest.builder()
                .name("JaneDoe")
                .mail("jane@example.com")
                .password("securePass123")
                .age(22)
                .isMale(false)
                .phoneNo("1234567890")
                .build();

        AuthResponse mockResponse = AuthResponse.builder()
                .id("u2")
                .name("JaneDoe")
                .token("fake-jwt-token-register")
                .mail("jane@example.com")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        // WHEN & THEN: Perform POST /api/auth/register and verify response
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token-register"))
                .andExpect(jsonPath("$.name").value("JaneDoe"));
    }
}
