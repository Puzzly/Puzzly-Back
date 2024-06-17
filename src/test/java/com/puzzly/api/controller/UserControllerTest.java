package com.puzzly.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void initMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void createUser() throws Exception {

        UserRequestDto userDto = new UserRequestDto();
        userDto.setUserName("유저 테스트 명");
        userDto.setNickName("유저 닉테임");
        userDto.setEmail("email@naver.com");
        userDto.setPassword("password");
        userDto.setPhoneNumber("010-1234-5678");
        userDto.setBirth(null);
        userDto.setGender(true);
        userDto.setFirstTermAgreement(true);
        userDto.setSecondTermAgreement(true);
        userDto.setStatusMessage("WELCOME PUZZLY");

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // 실제 인자 캡처 & 검증
        ArgumentCaptor<UserRequestDto> captor = ArgumentCaptor.forClass(UserRequestDto.class);
        BDDMockito.verify(userService).createUser(captor.capture());

        // Get the captured argument
        UserRequestDto capturedArgument = captor.getValue();

        // Verify the captured argument
        assertEquals("유저 테스트 명", capturedArgument.getUserName());
        assertEquals("유저 닉테임", capturedArgument.getNickName());
        assertEquals("email@naver.com", capturedArgument.getEmail());
        assertEquals("password", capturedArgument.getPassword());
        assertEquals("010-1234-5678", capturedArgument.getPhoneNumber());
        assertEquals(null, capturedArgument.getBirth());
        assertEquals(true, capturedArgument.getGender());
        assertEquals(true, capturedArgument.getFirstTermAgreement());
        assertEquals(true, capturedArgument.getSecondTermAgreement());
        assertEquals("WELCOME PUZZLY", capturedArgument.getStatusMessage());

    }

}