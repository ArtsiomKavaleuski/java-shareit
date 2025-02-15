package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @MockBean
    private UserClient userClient;
    @Autowired
    private ObjectMapper objectMapper;

    public UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder().id(1L).name("John Doe").email("test@test.com").build();
    }

    @Test
    public void addUserSuccess() throws Exception {
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void addUserWithInvalidEmailShouldReturnBadRequest() throws Exception {
        userDto.setEmail("test.ru");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyEmailShouldReturnBadRequest() throws Exception {
        userDto.setEmail(null);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyNameShouldReturnBadRequest() throws Exception {
        userDto.setEmail(null);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserSuccess() throws Exception {
        UserDto newUser = userDto;
        newUser.setName("Updated name");
        mockMvc.perform(patch("/users/1")
                .content(objectMapper.writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserSuccess() throws Exception {
        mockMvc.perform(delete("/users/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void getUserByIdSuccess() throws Exception {
        mockMvc.perform(get("/users/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void getAllUsersSuccess() throws Exception {
        mockMvc.perform(get("/users")
                )
                .andExpect(status().isOk());
    }
}
