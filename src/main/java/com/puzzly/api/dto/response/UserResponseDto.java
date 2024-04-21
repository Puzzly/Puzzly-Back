package com.puzzly.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.dto.request.UserExRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private boolean gender;
    private AccountAuthority accountAuthority;

    // JSON Format 쓰기 전엔 아래 코드로 대응했음
    // getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    // Json으로 올려받고 내려줄때 패턴 선언, String 까지 써야 Swagger가 알아들음
    // REFER : https://nelljundev.tistory.com/217
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDateTime;
    private String status;

    // 사용자 추가정보
    private UserExResponseDto userExResponseDto;

}
