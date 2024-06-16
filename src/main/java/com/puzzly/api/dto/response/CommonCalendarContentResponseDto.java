package com.puzzly.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "공공 데이터 : 일정에 관련된 응답을 전달할 때 사용하는 DTO")
public class CommonCalendarContentResponseDto {
    @Schema(description = "캘린더 컨텐트(일정) PK", defaultValue = "0")
    private Long contentId;
    @Schema(pattern = "2024-04-21 00:00:00", type="string", description = "일정이 시작되는 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;
    @Schema(pattern = "2024-04-22 00:00:00", type="string", description = "일정이 종료되는 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;
    @Schema(description = "일정 제목")
    private String title;
    @Schema(description = "일정 종류")
    private String type;
    @Schema(description = "휴일 여부")
    private Boolean isHoliday;
}
