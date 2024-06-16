package com.puzzly.api.controller;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarContentRequestDto;
import com.puzzly.api.dto.request.CalendarLabelRequestDto;
import com.puzzly.api.dto.request.CalendarRequestDto;
import com.puzzly.api.dto.response.CalendarContentResponseDto;
import com.puzzly.api.dto.response.CalendarLabelResponseDto;
import com.puzzly.api.dto.response.CalendarResponseDto;
import com.puzzly.api.dto.response.CommonCalendarContentResponseDto;
import com.puzzly.api.dto.wrapper.RestResponse;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "02.Calendar", description="Calendar Operations\n\n사용DTO: CalendarRequestDto, CalendarResponseDto, CalendarContentRequestDto, CalendarContentResponseDto\n\nDTO설명은 이 페이지 최 하단부 schema 설명 참조\n\n 일반적으로 응답은 status, message, timestamp, result의 key를 가지는 map 구조임")
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping("/invitationCode")
    @Operation(summary="캘린더 초대코드 생성, 토큰필요 O", description = "캘린더 초대코드 생성, 토큰필요 O",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content={@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schemaProperties = {
                                    @SchemaProperty(name = "calendarId", schema = @Schema(type="string", defaultValue="1", requiredMode = Schema.RequiredMode.REQUIRED))
                                })
                    }
            )
    )
    @ApiResponse(responseCode = "200", description = "성공시 result 내 key:inviteCode의 value로 초대 코드 제공")
    public ResponseEntity<?> createInvitationCode(
            HttpServletRequest request,
            @RequestBody Map<String, Long> requestMap
    ) throws FailException, Exception{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, String> resultMap = calendarService.createInviteCode(securityUser, MapUtils.getLong(requestMap, "calendarId"));
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PostMapping("/join")
    @Operation(summary="캘린더 초대코드로 가입, 토큰필요 O", description = "캘린더 초대코드로 가입, 토큰필요 O",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content={@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schemaProperties = {
                                        @SchemaProperty(name = "inviteCode", schema = @Schema(type="string", defaultValue="", requiredMode = Schema.RequiredMode.REQUIRED))
                                    })
                    }
            )
    )
    @ApiResponse(responseCode = "200", description = "성공시 result 내 key:calendar 의 value로 가입에 성공한 캘린더 정보 제공")
    public ResponseEntity<?> joinByInvitationCode(
            HttpServletRequest request,
            @RequestBody HashMap<String, String> map
    )throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.joinCalendarByInviteCode(securityUser, map.get("inviteCode"));
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping("/list")
    @Operation(summary = "내가 참여한 켈린더 목록 조회, 토큰필요 O", description = "내가 참여한 모든 켈린더 조회, 토큰필요 O")
    @ApiResponse(responseCode = "200", description = "성공시 result 내 key:calendarList 의 value로 캘린더 리스트 제공")
    public ResponseEntity<?> getCalendarList(
            HttpServletRequest request,
            @Parameter(description="페이지 번호, 0부터 시작, 파라미터로 주어지지 않으면 BE에서 자동으로 0으로 셋팅")
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description="한번에 가져갈 갯수, 파라미터로 주어지지 않으면 BE에서 자동으로 10으로 셋팅")
            @RequestParam(required = false, defaultValue = "10") int pageSize
    )throws FailException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.getCalendarList(securityUser, offset, pageSize, false);
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PostMapping()
    @Operation(summary = "캘린더 생성, 토큰필요 O", description = "캘린더 생성, 토큰 필요 O")
    @ApiResponse(responseCode = "200", description = "성공시 result 내 key:calendar의 value 로 캘린더 정보 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarResponseDto.class)))
    public ResponseEntity<?> createCalendar(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* calendarId (생략가능, 서버에서 자동으로 값을 생성함)\n\n" +
                            "* calendarType (생략 가능, 값 생성/변경은 서버에서 수행함. 값이 주어지면 무시됨)\n\n"+
                            "상기 명시되지 않은 값을 생략할 경우 400에러 발생\n\n" +
                            "이 API에서 주의사항은 아래와 같음\n\n" +
                            "* calendarName (생략되었거나 빈 값인경우 400에러 발생)"
            )
            @RequestBody CalendarRequestDto calendarRequestDto
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.createCalendar(securityUser, calendarRequestDto);
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping()
    @Operation(summary = "캘린더 수정, 토큰필요 O", description = "캘린더 수정, 토큰 필요 O")
    @ApiResponse(responseCode = "200", description = "성공시 result 내 key:calendar 의 value로 캘린더 정보 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarResponseDto.class)))
    public ResponseEntity<?> modifyCalendar(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* calendarType (생략 가능, 값 생성/변경은 서버에서 수행함. 값이 주어지면 무시됨)\n\n"+
                            "상기 명시되지 않은 값을 생략할 경우 400에러 발생\n\n" +
                            "이 API에서 주의사항은 아래와 같음\n\n" +
                            "* calendarName (빈 값인경우 400에러 발생)"
            )
            @RequestBody CalendarRequestDto calendarRequestDto
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.modifyCalendar(securityUser, calendarRequestDto);
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping()
    @Operation(summary = "캘린더 삭제, 토큰필요 O", description = "캘린더 삭제, 내가 생성한 캘린더만 삭제할 수 있음")
    @ApiResponse(responseCode = "200", description = "성공시 result 내 key:calendarId 의 value로 삭제한 캘린더의 Id(PK) 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarResponseDto.class)))
    public ResponseEntity<?> removeCalendarList(
            HttpServletRequest request,
            @Parameter(description="삭제하려는 CalendarId")
            @RequestParam Long calendarId
    )throws FailException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.removeCalendar(securityUser, calendarId);
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/content")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:content 의 value로 캘린더 컨텐트(일정) 제공", content = @Content(schema = @Schema(implementation = CalendarContentResponseDto.class)))
    @Operation(summary="캘린더 컨텐트(일정) 등록, JWT 토큰 필요", description = "캘린더 컨텐트(일정) 등록, JWT토큰 필요")
    public ResponseEntity<?> createCalendarContent(
            HttpServletRequest request,
            @Parameter(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* ContentId (생략 가능, 값 생성/변경은 서버에서 수행함. 값이 주어지면 무시됨)\n\n"+
                            "상기 명시되지 않은 값을 생략할 경우 400에러 발생\n\n" +
                            "이 API에서 주의사항은 아래와 같음\n\n" +
                            "alarmType: 알림 설정 (같은 시간: SAME_TIME, 10분전: TEN_MINUTES_BEFORE, 1시간전: ONE_HOUR_BEFORE, 1일전: ONE_DAY_BEFORE, 직접설정: CUSTOM)\n\n" +
                            "",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody CalendarContentRequestDto contentRequestDto
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.createCalendarContent(securityUser, contentRequestDto);
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping("/content/list")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:contentList value로 캘린더 컨텐트(일정) 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarContentResponseDto.class)))
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:commonList value로 공통일정 (휴일정보) 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommonCalendarContentResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 컨텐트(일정) 리스트 가져오기, JWT 토큰 필요", description = "캘린더 컨텐트(일정) 리스트 가져오기, JWT 토큰 필요")
    public ResponseEntity<?> getCalendarContent(
            HttpServletRequest request,
            @Parameter(description="조회하려는 캘린더의 PK list, 주어지지 않으면 요청 거절")
            @RequestParam(name="calendarId", required = true) ArrayList<Long> calendarId,
            @Parameter(description="조회하려는 일정의 범위 시작 시각 필수값", required = true)
            @RequestParam(name="startTargetDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") String startTargetDateTimeString,
            @Parameter(description="조회하려는 일정의 범위 끝 시각 필수값", required = true)
            @RequestParam(name="limitTargetDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") String limitTargetDateTimeString

    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        LocalDateTime startTargetDateTime = LocalDateTime.parse(startTargetDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime limitTargetDateTime = LocalDateTime.parse(limitTargetDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        HashMap<String, Object> resultMap = calendarService.getCalendarContentList(securityUser, calendarId, startTargetDateTime, limitTargetDateTime, false);

        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/content")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:content의 value로 캘린더 컨텐트(일정) 제공", content = @Content(schema = @Schema(implementation = CalendarContentResponseDto.class)))
    @Operation(summary="캘린더 컨텐트(일정) 조회, JWT 토큰 필요", description = "캘린더 컨텐트(일정) 조회, JWT토큰 필요")
    public ResponseEntity<?> getCalendarContent(
            HttpServletRequest request,
            @Parameter(description="캘린더 컨텐츠 Id")
            @RequestParam(name= "contentId") Long contentId
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.getCalendarContent(securityUser, contentId);
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PutMapping(value="/content")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:content의 value로 캘린더 컨텐트(일정) 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarContentResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 컨텐트(일정) 수정, JWT 토큰 필요", description = "캘린더 컨텐트(일정) 수정하기, JWT 토큰 필요")
    public ResponseEntity<?> modifyCalendarContent(
            HttpServletRequest request,
            @Parameter(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* 변경이 발생하지 않은 Parameter값은 서버로 보내지 않고 생략 가능"+
                            "상기 명시되지 않은 값을 생략할 경우 400에러 발생\n\n" +
                            "이 API에서 주의사항은 아래와 같음\n\n"+
                            "* 반복 정보가 변경 될 경우, 이 API에서는 변경된 내용만 보내는 것이 아니라 전체 내용을 다시 보내야함 (삭제후 생성하는 방식 사용)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody CalendarContentRequestDto contentRequestDto
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        HashMap<String, Object> resultMap = calendarService.modifyCalendarContent(securityUser, contentRequestDto);

        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @DeleteMapping(value="/content")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:contentId value로 삭제된 켈린더 컨텐트(일정) Id 제공")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 컨텐트 삭제, JWT 토큰 필요", description = "캘린더 컨텐츠 삭제, JWT 토큰 필요")
    public ResponseEntity<?> removeCalendarContent(
            HttpServletRequest request,
            @Parameter(description="캘린더 컨텐트 Id")
            @RequestParam(name= "contentId") Long contentId
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        HashMap<String, Object> resultMap = calendarService.removeCalendarContent(securityUser, contentId);

        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/content/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:attachmentsIdList 의 value로 업로드 성공한 첨부파일 Id 제공")
    @Operation(summary="캘린더 첨부파일 업로드", description = "캘린더 첨부파일 업로드, JWT토큰 필요")
    public ResponseEntity<?> uploadCalendarContentAttachments(
            HttpServletRequest request,
            //@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(required = true) List<MultipartFile> attachmentsList
    ) throws FailException, IOException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.uploadCalendarContentAttachments(securityUser, attachmentsList);
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/content/attachments")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    @Operation(summary="캘린더 첨부파일 다운로드", description = "캘린더 첨부파일 다운로드, JWT토큰 필요, 해당 캘린더에 참여해있어야 함")
    public void downloadCalendarContentAttachments(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description="첨부파일 Id")
            @RequestParam(name="attachmentsId") Long attachmentsId
    ) throws FailException, IOException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        calendarService.downloadCalendarContentAttachments(securityUser, attachmentsId, request, response);
    }

    @PostMapping("label")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:content 의 value로 캘린더 라벨 제공", content = @Content(schema = @Schema(implementation = CalendarLabelResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 라벨 등록, JWT 토큰 필요", description = "캘린더 라벨 등록, JWT토큰 필요")
    public ResponseEntity<?> createCalendarLabel(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* labelId (생략가능, 서버에서 자동으로 값을 생성함)\n\n" +
                            "* orderNum (생략, 서버에서 자동으로 값을 생성함)\n\n" +
                            "* colorCode (생략 가능, default color: #000000)\n\n"+
                            "상기 명시되지 않은 값을 생략할 경우 400에러 발생\n\n" +
                            "이 API에서 주의사항은 아래와 같음\n\n" +
                            "* labelName (생략되었거나 빈 값, 증복된 값인 경우 400에러 발생)\n\n" +
                            "* colorCode (헥사코드 포맷 벗어난 경우 에러.)\n\n"
            )
            @RequestBody CalendarLabelRequestDto calendarContentsLabel
    ) throws FailException, Exception{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        CalendarLabelResponseDto calendarLabelResponseDto = calendarService.createCalendarLabel(securityUser, calendarContentsLabel);
        restResponse.setResult(calendarLabelResponseDto);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping("label")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:content 의 value로 캘린더 라벨 리스트 제공", content = @Content(schema = @Schema(implementation = CalendarLabelResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="내가 참여한 라벨 목록 조회, 토큰필요 O, JWT 토큰 필요", description = "내가 참여한 모든 라벨 조회, JWT토큰 필요")
    public ResponseEntity<?> getCalendarLabelList(
            HttpServletRequest request,
            @Parameter(description="페이지 번호, 0부터 시작, 파라미터로 주어지지 않으면 BE에서 자동으로 0으로 셋팅")
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description="한번에 가져갈 갯수, 파라미터로 주어지지 않으면 BE에서 자동으로 10으로 셋팅")
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description="캘린더 ID, 필수 값")
    @RequestParam(required = true, defaultValue = "1") Long calendarId
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = calendarService.getCalendarLabelList(securityUser, calendarId, offset, pageSize);
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PutMapping(value="/label")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:content의 value로 캘린더 라벨 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarContentResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 라벨 수정, JWT 토큰 필요", description = "캘린더 라벨 수정하기, JWT 토큰 필요")
    public ResponseEntity<?> modifyCalendarLabel(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* 변경이 발생하지 않은 Parameter값은 서버로 보내지 않고 생략 가능\n\n"+
                            "이 API에서 주의사항은 아래와 같음\n\n" +
                            "* orderNum (label orderNum -+ 이동한 순서. 0 이하, max order num 범위 벗어난 경우 에러.)\n\n" +
                            "* colorCode (헥사코드 포맷 벗어난 경우 에러.)\n\n"
            )
            @RequestBody CalendarLabelRequestDto calendarLabel
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        HashMap<String, Object> resultMap = calendarService.modifyCalendarlabel(securityUser, calendarLabel);

        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @DeleteMapping(value="/label")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:labelId value로 삭제된 켈린더 라벨 Id 제공")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 라벨 삭제, JWT 토큰 필요", description = "캘린더 라벨 삭제, JWT 토큰 필요")
    public ResponseEntity<?> removeCalendarLabel(
            HttpServletRequest request,
            @Parameter(description="캘린더 라벨 Id")
            @RequestParam(name= "labelId") Long labelId
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        HashMap<String, Object> resultMap = calendarService.removeCalendarLabel(securityUser, labelId);

        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "test for open calendar")
    public ResponseEntity<?> getOpenCal(
            @RequestParam(name = "month") String month,
            @RequestParam(name = "year") String year
    ) throws Exception {
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", calendarService.pullOpenCalendar(year, month));

        return new ResponseEntity<>(restResponse,HttpStatus.OK);
    }
}
