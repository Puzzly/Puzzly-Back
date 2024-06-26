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

    @Schema(description = "라벨 생성자 PK", defaultValue = "1")
    private long createId;
    @Schema(description = "라벨 생성자 닉네임", defaultValue = "김퍼즐리")
    private String createNickName;
    @Schema(description = "라벨 생성 시각", pattern = "2024-04-22 00:00:00", type="string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;

    @Schema(description = "라벨 수정자 PK", defaultValue = "1")
    private long modifyId;
    @Schema(description = "라벨 수정자 닉네임", defaultValue = "김퍼즐리")
    private String modifyNickName;
    @Schema(description = "라벨 수정 시각", pattern = "2024-04-22 00:00:00", type="string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDateTime;
}
