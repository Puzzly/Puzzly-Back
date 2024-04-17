package com.puzzly.api.dto.request;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Builder
public class UserExRequestDto {
    private Long userExId;

    private boolean firstTermAgreement;
    private boolean secondTermAgreement;
    private String statusMessage;
    private String profileFilePath;
}
