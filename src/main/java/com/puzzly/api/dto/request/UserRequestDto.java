package com.puzzly.api.dto.request;

import com.puzzly.api.enums.AccountAuthority;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
public class UserRequestDto {
    private Long userId;
    private String userName;
    private String nickName;
    private String email;
    private String password;
    private String phoneNumber;
    private LocalDate birth;
    private boolean gender;
    private AccountAuthority accountAuthority;
    private LocalDateTime createDateTime;
    private LocalDateTime modifyDateTime;
    private LocalDateTime deleteDateTime;
    private String status;

    private UserExRequestDto userExRequestDto;
    /*
    public static UserDTO toEntity(User entity){
        return UserDTO.builder()
                .userId(entity.getUserId())
                .username(entity.getUserName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .createTime(entity.getCreateTime())
                .modifyTime(entity.getModifyTime())
                .deleteTime(entity.getDeleteTime())
                .build();
    }
     */
}
