package com.puzzly.api.member.dto;

import com.puzzly.api.enums.Authority;
import com.puzzly.api.enums.JoinType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDTORequest {
    private String email;
    private String password;
    private String userName;
    private LocalDate birth;
    private boolean gender;
    private String phoneNumber;
    private JoinType joinType;
    private LocalDateTime createDatetime;
    private Authority authority;

    /*
    public static UserDTO toEntity(User entity){
        return UserDTO.builder()
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .createTime(entity.getCreateTime())
                .modifyTime(entity.getModifyTime())
                .deleteTime(entity.getDeleteTime())
                .build();
    }
     */
}
