package com.puzzly.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "라벨에 관련된 응답을 전달할 때 사용하는 DTO")
public class CalendarLabelResponseDto {

    @Schema(description = "라벨 PK", defaultValue = "1")
    private long labelId;

    @Schema(description = "라벨 이름")
    private String labelName;
    @Schema(description = "라벨 색상", defaultValue = "#000000")
    private String colorCode;
    @Schema(description = "라벨 순서", defaultValue = "1")
    private Integer orderNum;

    @Schema(description = "캘린더에 소속된 사용자 리스트")
    private List<UserResponseDto> userList = new ArrayList<>();
}
