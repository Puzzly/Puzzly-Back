package com.puzzly.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarLabelRequestDto;
import com.puzzly.api.service.CalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



//@WebMvcTest(CalendarController.class)
@ExtendWith(MockitoExtension.class)
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @MockBean
//    private CalendarService calendarService;

    @InjectMocks
    private CalendarController calendarController;

    @Mock
    private CalendarService calendarService;

//    @MockBean
//    private JwtUtils jwtUtils;

    ObjectMapper objectMapper = new ObjectMapper();

//    public Collection<? extends GrantedAuthority> getSimpleAuthorityListFromJwt(List<String> accountAuthorityArrayList) {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        for(String authority : accountAuthorityArrayList){
//            authorities.add(new SimpleGrantedAuthority(authority));
//        }
//        return authorities;
//    }

    @BeforeEach
    public void initMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(calendarController)
                .build();
    }

//    @BeforeEach
//    @WithMockUser(username = "admin@puzzly.com", roles = {"ADMIN"})
//    public void setUp() {
//        User user = User.builder().userId(1L).email("admin@puzzly.com").password("admin").build();
//
////        SecurityUser securityUser = new SecurityUser(user);
//
//        SecurityUser securityUser = new SecurityUser(user);
////        securityUser.setAuthorities(getSimpleAuthorityListFromJwt(authorities));
////        Authentication authToken = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
//
////        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        // 가짜 인증 토큰을 생성합니다.
//        Authentication authToken =
//                new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
//
//        // 보안 컨텍스트를 설정합니다.
////        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
////        securityContext.setAuthentication(authToken);
////        SecurityContextHolder.setContext(securityContext);
//
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//    }

    @Test
//    @WithMockUser(username = "admin@puzzly.com", roles = {"ADMIN"})
    void createCalendarLabel() throws Exception {
        CalendarLabelRequestDto labelDto = new CalendarLabelRequestDto();
        labelDto.setLabelName("퍼즐리 라벨");
        labelDto.setColorCode("#000000");
        labelDto.setOrderNum(1);
        labelDto.setCalendarId(1L);

//        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("securityUser.getUser(): " + securityUser.getUser());
        System.out.println("securityUser.getUsername(): " + securityUser.getAuthorities());

//        String token = jwtUtils.generateJwtToken(securityUser.getUser());

//        System.out.println("token: " + token);



        mockMvc.perform(
                post("/api/calendar/label")
                        .content(objectMapper.writeValueAsString(labelDto))
                        .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFkbWluQHB1enpseS5jb20iLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwiaWF0IjoxNzE3ODM3OTUyLCJpc3MiOiJwdXp6bHkiLCJleHAiOjE3MTc4NDM5NTJ9.NBABN5oiZKguB3EzE6Ew-OxNRDpi8QFXEyPc-2k34k8")
                        .contentType("application/json")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//                .andExpect(jsonPath("$[0].id", is("1")))
//                .andExpect(jsonPath("$[0].name", is("John")));

        verify(calendarService).createCalendarLabel(securityUser, labelDto);
    }

    @Test
    void getCalendarLabelList() {
    }

    @Test
    void modifyCalendarLabel() {
    }

    @Test
    void removeCalendarLabel() {
    }
}