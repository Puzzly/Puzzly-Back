package com.puzzly.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "캘린더 일정 반복 정보")
public class CalendarContentRecurringInfoRequestDto {

    @Schema(description = "PK")
    private Long infoId;

    @Schema(description = "캘린더 컨텐트 PK")
    private Long calendarContentId;

    @Schema(description = "반복 종류, D(매일), W(매주), M(매월), Y(매년)")
    private String recurringType;

    @Schema(description =" recurringType M,W,Y 반복주기, M : 0 -> 매월 1,2,3,4 -> 매월 n째주")
    private Integer period;

    @Schema(description = "반복 요일 1 : 월 , 7:일 구분자 ,")
    private String recurringDate;

    @Schema(description = "반복할 일자")
    private Integer recurringDay;

    @Schema(description = "반복 횟수 종료 조건")
    private Integer conditionCount;

    @Schema(pattern = "2024-04-22", type="string", description = "일정이 종료되는 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate conditionEndDate;

}
