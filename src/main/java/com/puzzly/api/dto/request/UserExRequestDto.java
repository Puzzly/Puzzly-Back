package com.puzzly.api.dto.request;

import com.puzzly.api.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserExRequestDto {

    private Long userExId;

    private Boolean firstTermAgreement;
    private Boolean secondTermAgreement;
    private String statusMessage;
    private String profileFilePath;

}
