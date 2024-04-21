package com.puzzly.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private Long userId;
    private String userName;
    private String nickName;
    private String email;
    private String password;
    private String phoneNumber;
    // Date는 T, Z 등 불필요한 요소가 swagger-ui에서 안붙어서 별도 명시 X
    //@JsonFormat(pattern = "yyyy-MM-dd")
    // REFER : https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=varkiry05&logNo=221736856257
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private Boolean gender;
    private AccountAuthority accountAuthority;
    //Swagger에 에시로 출력될 패턴
    // https://stackoverflow.com/questions/49379006/what-is-the-correct-way-to-declare-a-date-in-an-openapi-swagger-file/49379235#49379235
    @Schema(pattern = "2024-04-21 21:37:00", type="string")
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss",)
    // Json으로 올려받고 내려줄때 패턴 선언, String 까지 써야 Swagger가 알아들음
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;
/*
    //DTO는 해당 내용을 알 필요 없음
    @Schema(pattern = "yyyy-MM-dd HH:mm:ss", type="string", example="")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDateTime;
    @Schema(pattern = "yyyy-MM-dd HH:mm:ss", type="string", example = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDateTime;*/
    private String status;

    // 사용자 추가정보
    private UserExRequestDto userExRequestDto;


}
