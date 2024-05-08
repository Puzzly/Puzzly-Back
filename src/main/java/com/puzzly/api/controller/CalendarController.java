package com.puzzly.api.controller;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarContentsRequestDto;
import com.puzzly.api.dto.request.CalendarRequestDto;
import com.puzzly.api.dto.response.CalendarContentsResponseDto;
import com.puzzly.api.dto.response.CalendarResponseDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "02.Calendar", description="Calendar Operations\n\n사용DTO: CalendarRequestDto, CalendarResponseDto, CalendarContentsRequestDto, CalendarConetnsResponseDto\n\nDTO설명은 이 페이지 최 하단부 schema 설명 참조")
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    @GetMapping("/list")
    @Operation(summary = "내가 참여한 켈린더 목록 조회, 토큰필요 O", description = "내가 참여한 모든 켈린더 조회, 토큰필요 O")
    public ResponseEntity<?> getCalendarList(
            HttpServletRequest request,
            @Parameter(description="페이지 번호, 0부터 시작, 파라미터로 주어지지 않으면 BE에서 자동으로 0으로 셋팅")
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description="한번에 가져갈 갯수, 파라미터로 주어지지 않으면 BE에서 자동으로 10으로 셋팅")
            @RequestParam(required = false, defaultValue = "10") int pageSize
    )throws FailException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        List<CalendarResponseDto> calendarList = calendarService.getSimpleCalendarList(securityUser, offset, pageSize);
        restResponse.setResult(calendarList);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PostMapping()
    @Operation(summary = "캘린더 생성, 토큰필요 O", description = "캘린더 생성, 토큰 필요 O")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarResponseDto.class)))
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

        CalendarResponseDto calendar = calendarService.createCalendar(securityUser, calendarRequestDto);
        response.setResult(calendar);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping()
    @Operation(summary = "캘린더 수정, 토큰필요 O", description = "캘린더 수정, 토큰 필요 O")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarResponseDto.class)))
    public ResponseEntity<?> updateeCalendar(
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

        CalendarResponseDto calendar = calendarService.updateCalendar(securityUser, calendarRequestDto);
        response.setResult(calendar);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/invitationCode")
    @Operation(summary="캘린더 초대코드 생성, 토큰필요 O", description = "캘린더 초대코드 생성, 토큰필요 O, 현시점 유효시간 무제한, 향후 24시간으로 조정 예정",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        content={
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schemaProperties = {
                                                @SchemaProperty(name = "calendarId", schema = @Schema(type="string", defaultValue="1", requiredMode = Schema.RequiredMode.REQUIRED))
                                        }
                                )
                        }
                ))
    public ResponseEntity<?> createInvitationCode(
            HttpServletRequest request,
            @RequestBody Map<String, Long> requestMap
    ) throws FailException, Exception{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        String invitationCode = calendarService.createInviteCode(securityUser, MapUtils.getLong(requestMap, "calendarId"));
        restResponse.setResult(invitationCode);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PostMapping("/join")
    @Operation(summary="초대코드로 캘린더 가입, JWT토큰 필요", description = "초대코드로 캘린더 가입, JWT토큰 필요",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content={
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schemaProperties = {
                                    @SchemaProperty(name = "invitationCode", schema = @Schema(type="string", defaultValue="", requiredMode = Schema.RequiredMode.REQUIRED))
                            }
                    )
            }
    ))
    public ResponseEntity<?> joinByInvitationCode(
            HttpServletRequest request,
            @RequestBody HashMap<String, String> map
    )throws FailException, Exception{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        CalendarResponseDto calendar = calendarService.joinCalendarByInviteCode(securityUser, map.get("invitationCode"));
        restResponse.setResult(calendar);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping("/contents/list")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarContentsResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 일정 리스트 가져오기, JWT 토큰 필요", description = "캘린더 일정 리스트 가져오기, JWT 토큰 필요")
    public ResponseEntity<?> getCalendarContents(
            HttpServletRequest request,
            @Parameter(description="조회하려는 캘린더의 PK")
            @RequestParam(name="calendarId") Long calendarId,
            @Parameter(description="조회하려는 일정의 범위 시작 시각 필수값", required = true)
            @RequestParam(name="startTargetDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") String startTargetDateTimeString,
            @Parameter(description="조회하려는 일정의 범위 끝 시각 필수값", required = true)
            @RequestParam(name="limitTargetDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") String limitTargetDateTimeString

    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        LocalDateTime startTargetDateTime = LocalDateTime.parse(startTargetDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime limitTargetDateTime = LocalDateTime.parse(limitTargetDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<CalendarContentsResponseDto> calendarContentsList = calendarService.getCalendarContentsList(securityUser, calendarId, startTargetDateTime, limitTargetDateTime);

        restResponse.setResult(calendarContentsList);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/contents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = CalendarContentsResponseDto.class)))
    @Operation(summary="캘린더 컨텐츠 등록, JWT 토큰 필요", description = "캘린더 컨텐츠 등록, JWT토큰 필요")
    public ResponseEntity<?> createCalendarContents(
            HttpServletRequest request,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(required = false) List<MultipartFile> fileList,
            @Parameter(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* ContentsId (생략 가능, 값 생성/변경은 서버에서 수행함. 값이 주어지면 무시됨)\n\n"+
                            "상기 명시되지 않은 값을 생략할 경우 400에러 발생\n\n" +
                            "이 API에서 주의사항은 아래와 같음\n\n",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            // CalendarContents 변수가 FE에 노출되는 변수명이므로 RequestDto는 변수명에서 제거
            @RequestPart CalendarContentsRequestDto calendarContents
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        // FE에 노출되는 변수명으로 인해 변수명 재조정
        CalendarContentsResponseDto calendarContentsResponse = calendarService.createCalendarContents(securityUser, calendarContents, fileList);
        restResponse.setResult(calendarContentsResponse);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/contents")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = CalendarContentsResponseDto.class)))
    @Operation(summary="캘린더 컨텐츠 조회, JWT 토큰 필요", description = "캘린더 컨텐츠 등록, JWT토큰 필요")
    public ResponseEntity<?> getCalendarContents(
            HttpServletRequest request,
            @Parameter(description="일정 Id")
            @RequestParam(name="contentsId") Long contentsId
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        // FE에 노출되는 변수명으로 인해 변수명 재조정
        CalendarContentsResponseDto calendarContentsResponse = calendarService.getCalendarContents(securityUser, contentsId);
        restResponse.setResult(calendarContentsResponse);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @PutMapping(value="/contents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalendarContentsResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_* : 서비스 로직에서 의도된 체크 목록에 걸린것")
    @ApiResponse(responseCode = "400", description = "SERVER_MESSAGE_ 가 없는것 : 서비스 로직에 진입하지 못함. 파라미터 부족등으로 Controller에서 Spring이 튕긴것")
    @Operation(summary="캘린더 일정 수정하기, JWT 토큰 필요", description = "캘린더 일정 수정하기, JWT 토큰 필요")
    public ResponseEntity<?> updateCalendarContents(
            HttpServletRequest request,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(required = false) List<MultipartFile> fileList,
            @Parameter(
                    description="이 API에서 아래의 값은 생략이 가능함\n\n" +
                            "* 변경이 발생하지 않은 Parameter값은 서버로 보내지 않고 생략 가능"+
                            "상기 명시되지 않은 값을 생략할 경우 400에러 발생\n\n" +
                            "이 API에서 주의사항은 아래와 같음\n\n",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart CalendarContentsRequestDto calendarContents
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        CalendarContentsResponseDto calendarContentsResponse = calendarService.updateCalendarContents(securityUser, calendarContents, fileList);

        restResponse.setResult(calendarContentsResponse);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/download/file")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = CalendarContentsResponseDto.class)))
    @Operation(summary="캘린더 첨부파일 다운로드", description = "캘린더 첨부파일 다운로드, JWT토큰 필요, 해당 캘린더에 참여해있어야 함")
    public void downloadCalendarContentsAttachments(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description="첨부파일 Id")
            @RequestParam(name="contentsId") Long attachmentId
    ) throws FailException, IOException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();
        // FE에 노출되는 변수명으로 인해 변수명 재조정
        calendarService.downloadCalendarContentsAttachments(securityUser, attachmentId, request, response);
    }


    /*
    @PostMapping("/label")
    @Operation(summary = "캘린더 라벨 등록, JWT 토큰 필요", description = "캘린더 라벨 등록, JWT토큰 필요")
    public ResponseEntity<?> createCalendarLabel(
            HttpServletRequest request
    ) throws FailException{
        return null;
    }

     */
}
