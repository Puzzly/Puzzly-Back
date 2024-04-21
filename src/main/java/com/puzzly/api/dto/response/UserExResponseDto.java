package com.puzzly.api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserExResponseDto {
    boolean firstTermAgreement;
    boolean secondTermAgreement;
    private String statusMessage;
    private String profileFilePath;
}
