package com.puzzly.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@NoArgsConstructor

@Schema(description = "캘린더에 관련된 요청사항을 전달할 떄 사용하는 DTO")
public class CalendarRequestDto {
    @Schema(description = "calendar PK")
    private Long calendarId;

    @Schema(description = "개인캘린더 : PRIVATE / 팀캘린더 : TEAM, 서버에서 제어", hidden = true)
    private String calendarType;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "퍼즐리 캘린더", description = "calendar 이름")
    private String calendarName;

}
