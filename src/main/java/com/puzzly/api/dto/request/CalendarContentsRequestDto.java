package com.puzzly.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "일정에 관련된 요청사항을 전달할 때 사용하는 DTO")
public class CalendarContentsRequestDto {

    @Schema(description = "일정을 등록하려는 캘린더의 PK", defaultValue = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long calendarId;
    @Schema(description = "등록/관리 하려는 일정의 PK", defaultValue = "0")
    private Long contentsId;
    @Schema(description = "일정이 가질 Label ID, 생략할 수 있음, 단일 컨텐츠는 단일 라벨 아이디만 가질 수 있음", defaultValue = "0")
    private Long labelId;

    @Schema(description = "일정을 생성한 유저의 PK, 생략가능 서버에서 처리", hidden = true)
    private Long createId;
    @Schema(description = "일정을 생성한 유저의 PK, 생략가능 서버에서 처리", hidden = true)
    private Long modifyId;
    @Schema(description = "일정을 삭제한 유저의 PK, 생략가능 서버에서 처리", hidden = true)
    private Long deleteId;

    @Schema(pattern = "2024-04-21 00:00:00", type="string", description = "일정이 시작되는 시각")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;
    @Schema(pattern = "2024-04-22 00:00:00", type="string", description = "일정이 종료되는 시각")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Schema(description = "일정 제목", defaultValue = "puzzly 전체회의")
    private String title;
    @Schema(description = "일정 본문", defaultValue = "puzzly BE, FE, 기획, 디자인 팀 전체회의")
    private String contents;
    @Schema(description = "알림 여부, 생략하면 자동으로 서버에서  false로 등록", defaultValue = "false")
    private Boolean notify;
    @Schema(pattern = "2024-04-21 12:00:00", type="string", description = "알림 발송 시간, notify가 true일 경우에만 주어진 값이 수용됨.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime notifyTime;
    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "calendar 생성시간", hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;

    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "calendar 수정시간", hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDatetime;
    @Schema(description = "일정 메모", defaultValue = "날짜 변동 가능성 있음")
    private String memo;
    @Schema(description = "일정이 진행될 위치, 위경도값, \",\"로 위경도 구분", defaultValue = "")
    private String location;

    @Schema(description = "캘린더에 등록할 파일 PK List", defaultValue = "")
    private ArrayList<Long> createAttachmentsList;
    @Schema(description = "삭제할 파일 PK List", defaultValue = "")
    private ArrayList<Long> deleteAttachmentsList;
}
