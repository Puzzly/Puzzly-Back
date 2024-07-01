package com.puzzly.api.controller;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.dto.wrapper.RestResponse;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "01. User", description = "사용자 관련 Operation\n\n 사용 DTO : UserRequestDto, UserResponseDto\n\n DTO 설명은 이 페이지 최 하단부 schema 설명 참조")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "토큰 필요X / 성공하면 result.user 의 value로 사용자 정보 리턴")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:user 의 value로 사용자 정보 제공")
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> createUser(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능하며, 서버에서 default value를 생성함\n\n" +
                            "* userId (사용자 PK, ROLE_USER API에서는 생략)\n\n"+
                            "* accountAuthority (생략가능, 생략할 경우 자동으로 ROLE_USER, 가능한 다른 값 : ROLE_ADMIN \n\n" +
                            "* 상기 명시되지 않은 파라미터가 생략되면 400에러 발생 \n\n"
            )
            @RequestBody UserRequestDto userRequestDto
    ) throws FailException {
        RestResponse response = new RestResponse();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userService.createUser(userRequestDto));
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value="/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 프로필 사진 업로드", description = "내 프로필 사진 업로드. 토큰필요 O / 성공시 result.user의 value로 사용자 정보 리턴")
    @ApiResponse(responseCode = "200", description = "성공시 result에 key:attachmentsId 의 value로 업로드 성공한 첨부파일 id 제공")
    public ResponseEntity<?> uploadUserAttachments(
            HttpServletRequest request,
            @RequestPart(required = true) MultipartFile attachments
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userService.uploadUserProfile(securityUser, attachments));
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    @GetMapping("/email")
    @Operation(summary = "회원가입 시 email 중복확인", description = "이메일 중복확인 / 토큰필요X")
    @ApiResponse(responseCode = "200", description = " 중복없음 ")
    @ApiResponse(responseCode = "400", description = " 중복있음 ")
    public ResponseEntity<?> selectExistsEmail(
            HttpServletRequest request,
            @Parameter(description = "email")
            @RequestParam(name="email", required = true) String email
    ) throws FailException {
        RestResponse response = new RestResponse();
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("result", userService.selectExistsEmail(email));
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/id")
    @Operation(summary = "회원가입 시 Id 중복확인", description = "계정Id 중복확인 / 토큰필요X")
    @ApiResponse(responseCode = "200", description = " 중복 없음 ")
    @ApiResponse(responseCode = "400", description = " 중복 있음 ")
    public ResponseEntity<?> selectExistsMemberId(
            HttpServletRequest request,
            @Parameter(description = "memberId")
            @RequestParam(name="memberId", required = true) String memberId
    ) throws FailException {
        RestResponse response = new RestResponse();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", userService.selectExistsMemberId(memberId));

        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    @Operation(summary = "내 정보 조회", description = "내 정보 조회, 토큰 필요 O / 성공시 result.user의 value로 사용자 정보 리턴")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:user의 value로 사용자 정보 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> getUser(
            HttpServletRequest request
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userService.getUser(securityUser.getUser().getUserId()));
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/profile")
    @Operation(summary = "회원 프로필 사진 다운로드", description = "회원 프로필 사진 다운로드, 토큰필요" +
            "\n\n 두개 다 활성 시 오류발생")
    @ApiResponse(responseCode="200", description = "성공시 프로필 사진 제공")
    public void getUserProfile(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "userId")
            @RequestParam(name="userId", required = false) Long userId
            /*
            @Parameter(description = "profilePath")
            @RequestParam(name="profilePath", required = false) String profilePath

             */
    ) throws IOException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userService.downloadUserProfile(securityUser, userId, request, response);
    }

    @PutMapping()
    @Operation(summary = "내 정보 변경", description = "내 정보 수정, 토큰 필요 O / 성공시 result.user의 value로 사용자 정보 리턴")
    public ResponseEntity<?> modifyUser(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 수정 가능한 값은 아래와 같음. 다른 값은 주어져도 무시됨\n\n"+
                            "nickName(별명), statusMessage(상태메시지), firstTermAgreement, SecondTermAgreement(약관동의여부)"
            )
            @RequestBody UserRequestDto userRequestDto
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userService.modifyUser(securityUser.getUser().getUserId(), userRequestDto));
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    @Operation(summary = "회원탈퇴", description = "회원탈퇴")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:user 의 value로 사용자 정보 제공")
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> deleteUser(
            HttpServletRequest request
    ) throws FailException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("result",userService.deleteUser(securityUser));
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("fcm-token")
    @Operation(summary = "FCM 토큰 변경", description = "2개월 동안 변동 없는 경우 fcm 토큰 자동 삭제")
    @ApiResponse(responseCode = "200", description = "성공시 성공 메세지 제공")
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> modifyFcmToken(
        HttpServletRequest request,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description="이 API에서 아래의 값은 생략이 가능함.\n\n" +
                "userId (사용자 PK, 생략시 본인계정 갱신. 계정 PK 주어질경우 관리자권한 검사 후 수행)\n\n"+
                "* 상기 명시되지 않은 파라미터가 생략되면 400에러 발생 \n\n"
        )
        @RequestParam(name ="userId", required=false) Long userId,
        @RequestParam(name ="appToken", required=false) String appToken,
        @RequestParam(name ="webToken", required=false) String webToken
    ) throws FailException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        HashMap<String, Object> resultMap = userService.modifyFcmToken(securityUser, userId, appToken, webToken);

        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: 토큰 존재 여부 확인 API



}
