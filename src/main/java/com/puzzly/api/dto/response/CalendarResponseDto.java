package com.puzzly.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder

@AllArgsConstructor
@NoArgsConstructor

@Schema(description = "캘린더에 관련된 답변을 전달할 떄 사용하는 DTO")
public class CalendarResponseDto {

    @Schema(description = "캘린더 PK", defaultValue = "1")
    private long calendarId;

    @Schema(description = "캘린더 타입 (PRIVATE: 1명 참여 / TEAM: 2명이상 참여)")
    private String calendarType;
    @Schema(description = "캘린더 이름", defaultValue = "퍼즐리 캘린더")
    private String calendarName;

    @Schema(description = "캘린더 생성자 PK", defaultValue = "1")
    private long createId;
    @Schema(description = "캘린더 생성자 닉네임", defaultValue = "김퍼즐리")
    private String createNickName;

    @Schema(description = "캘린더 수정자 PK", defaultValue = "1")
    private long modifyId;
    @Schema(description = "캘린더 수정자 닉네임", defaultValue = "김퍼즐리")
    private String modifyNickName;

    @Schema(description = "캘린더에 소속된 사용자 리스트")
    private List<UserResponseDto> userList = new ArrayList<>();

}
