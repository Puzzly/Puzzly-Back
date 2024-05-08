package com.puzzly.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.puzzly.api.entity.CalendarLabel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "일정에 관련된 응답을 전달할 때 사용하는 DTO")
public class CalendarContentsResponseDto {
    @Schema(description = "일정을 등록하려는 캘린더의 PK", defaultValue = "1")
    private Long calendarId;
    @Schema(description = "캘린더 이름")
    private String calendarName;
    @Schema(description = "등록/관리 하려는 일정의 PK", defaultValue = "0")
    private Long contentsId;
    @Schema(description = "일정이 가질 Label ID, 생략할 수 있음, 단일 컨텐츠는 단일 라벨 아이디만 가질 수 있음", defaultValue = "0")
    private Long labelId;

    @Schema(description = "일정 제목")
    private String title;
    //@Schema(description = "일정 라벨 정보")
    //private CalendarLabel calendarLabel;

    @Schema(description = "일정을 생성한 유저의 PK")
    private Long createId;

    @Schema(description = "일정을 생성한 유저의 닉네임")
    private String createNickName;

    @Schema(description = "일정을 수정한 유저의 PK")
    private Long modifyId;

    @Schema(description = "일정을 수정한 유저의 닉네임")
    private String modifyNickName;
    @Schema(description = "일정을 삭제한 유저의 PK")
    private Long deleteId;

    @Schema(description = "일정을 생성한 유저의 닉네임")
    private String deleteNickName;

    @Schema(pattern = "2024-04-21 00:00:00", type="string", description = "일정이 시작되는 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;
    @Schema(pattern = "2024-04-22 00:00:00", type="string", description = "일정이 종료되는 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Schema(description = "일정 본문", defaultValue = "puzzly 전체회의")
    private String contents;
    @Schema(description = "알림 여부, 생략하면 자동으로 서버에서  false로 등록", defaultValue = "false")
    private Boolean notify;
    @Schema(pattern = "2024-04-21 12:00:00", type="string", description = "알림 발송 시간, notify가 true일 경우에만 주어진 값이 수용됨.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime notifyTime;

    @Schema(description = "일정 메모", defaultValue = "날짜 변동 가능성 있음")
    private String memo;
    @Schema(description = "일정이 진행될 위치, 위경도값, \",\"로 위경도 구분", defaultValue = "")
    private String location;

    @Schema(pattern = "2024-04-22 00:00:00", type="string", description = "일정을 생성한 시각")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;
    @Schema(pattern = "2024-04-22 00:00:00", type="string", description = "일정을 생성한 시각")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDateTime;
    @Schema(pattern = "2024-04-22 00:00:00", type="string", description = "일정을 생성한 시각")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDateTime;

    // 굳이 DTO까지 필요할지 고민하다 map으로 처
    @Schema(description = "일정에 첨부되어있는 파일 리스트")
    private List<Map<String, Object>> fileList;

    // TODO 삭제된 파일 리스트도 보여줘야할지, 언제까지보여줘야할지, 평생 보관되어야하는지?
    @Schema(description = "일정에서 삭제된 파일 리스트, 현 시점Update API에서만 유효한 값을 가지며 이외에는 null")
    private List<Long> deleteFileList;
    //private List<Files> multipartFile;
}
