package com.puzzly.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.puzzly.api.domain.AccountAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "캘린더에 관련된 요청사항을 전달할 떄 사용하는 DTO")
public class CalendarRequestDto {
    // 서버에서 채집할것임, 0이어도 됨
    @Schema(hidden = true)
    private Long userId;
    // 최초생성인 경우 서버에서 채집할것임. 0이어도 됨.
    @Schema(description = "calendar PK")
    private Long calendarId;

    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "calendar 생성시각", hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;

    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "calendar 수정시각", hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDatetime;

    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "calendar 삭제 시각", hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDatetime;

    // 안쓸수도 있음
    @Schema(description = "개인캘린더 : PRIVATE / 팀캘린더 : TEAM, 서버에서 제어")
    private String calendarType;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "퍼즐리 캘린더", description = "calendar 이름")
    private String calendarName;

}
