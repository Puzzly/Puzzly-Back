package com.puzzly.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "캘린더에 관련된 답변을 전달할 떄 사용하는 DTO")
public class CalendarResponseDto {

    @Schema(description = "calendar PK", defaultValue = "1")
    private long calendarId;
    @Schema(description = "calendar 이름", defaultValue = "퍼즐리 캘린더")
    private String calendarName;

    @Schema(description = "calendar 생성자 PK", defaultValue = "1")
    private long createId;
    @Schema(description = "calendar 생성자 닉네임", defaultValue = "김퍼즐리")
    private String createNickName;

    @Schema(description = "개인켈린더 : PRIVATE / 팀캘린더 : TEAM, BE에서 생성 및 제어함")
    private String calendarType;

    @Schema(description = "calendar에 소속된 사용자 리스트")
    private List<UserResponseDto> userList = new ArrayList<>();

}
