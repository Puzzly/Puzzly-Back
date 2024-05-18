

package com.puzzly.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@NoArgsConstructor

@Schema(description = "라벨에 관련된 요청사항을 전달할 떄 사용하는 DTO")
public class CalendarLabelRequestDto {
    @Schema(description = "label PK")
    private Long labelId;

    @Schema(description = "라벨 이름", defaultValue = "퍼즐리 라벨")
    private String labelName;
    @Schema(description = "라벨 색상", defaultValue = "#000000")
    private String colorCode;
    @Schema(description = "라벨 순서", defaultValue = "1")
    private Integer orderNum;

    @Schema(description = "calendar PK", defaultValue = "1")
    private Long calendarId;
}
