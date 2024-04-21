package com.puzzly.api.dto.response;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.dto.request.UserExRequestDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String userName;
    private String nickName;
    private String email;
    private String phoneNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private boolean gender;
    private AccountAuthority accountAuthority;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDateTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDateTime;
    private String status;

    // 사용자 추가정보
    private UserExResponseDto userExResponseDto;

}
