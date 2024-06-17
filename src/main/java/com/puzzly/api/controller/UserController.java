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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "01. User", description = "사용자 관련 Operation\n\n 사용 DTO : UserRequestDto, UserResponseDto\n\n DTO 설명은 이 페이지 최 하단부 schema 설명 참조")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/email")
    @Operation(summary = "email 중복확인 / JWT토큰 필요X", description = "이메일 중복확인 / 토큰필요X")
    @ApiResponse(responseCode = "200", description = " result의 key exists false (중복없음) ")
    @ApiResponse(responseCode = "400", description = " 중복 있음 ")
    public ResponseEntity<?> existsEmail(
            HttpServletRequest request,
            @Parameter(description = "email")
            @RequestParam(name="email", required = true) String email
    ) throws FailException {
        RestResponse response = new RestResponse();
        HashMap<String, Object> resultMap = userService.selectExistsEmail(email);

        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/join")
    @Operation(summary = "회원가입 / JWT 토큰 필요 X", description = "회원가입, 토큰필요 X")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:user 의 value로 사용자 정보 제공")
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> createUser(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능하며, 서버에서 default value를 생성함\n\n" +
                            "userId (사용자 PK, ROLE_USER API에서는 생략)\n\n"+
                            "accountAuthority (생략가능, 생략할 경우 자동으로 ROLE_USER, 가능한 다른 값 : ROLE_ADMIN \n\n" +
                            "* 상기 명시되지 않은 파라미터가 생략되면 400에러 발생 \n\n"
            )
            @RequestBody UserRequestDto userRequestDto
            ) throws FailException {
        RestResponse response = new RestResponse();
        HashMap<String, Object> resultMap = userService.createUser(userRequestDto);

        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/user")
    @Operation(summary = "회원탈퇴", description = "회원탈퇴")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:user 의 value로 사용자 정보 제공")
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> deleteUser(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능함.\n\n" +
                            "userId (사용자 PK, 생략시 본인계정 탈퇴. 계정PK 주어질경우 관리자권한 검사 후 수행)\n\n"+
                            "* 상기 명시되지 않은 파라미터가 생략되면 400에러 발생 \n\n"
            )
            @RequestParam(name ="userId", required=false) Long userId
    ) throws FailException {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();
        HashMap<String, Object> resultMap = userService.deleteUser(securityUser, userId);

        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    @Operation(summary = "사용자 정보 조회, JWT 토큰필요 O", description = "내 정보 조회, 토큰 필요 O")
    @ApiResponse(responseCode = "200", description = "성공시 result의 key:user의 value로 사용자 정보 제공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> getUser(
            HttpServletRequest request,
            @Parameter(description = "조회하려는 사용자PK (생략 가능. 생략시 본인 정보 조회, 주어질경우 해당 유저 조회)")
            @RequestParam(name="userId", required = false) Long userId
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        HashMap<String, Object> resultMap = userService.getUser(userId == null ? securityUser.getUser().getUserId() : userId);
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping()
    @Operation(summary = "내 정보 변경하기", description = "내 정보 수정, 토큰 필요 O")
    public ResponseEntity<?> modifyUser(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능하며 변경사항이 발생하지 않은 값은 전송하지 않아도 됌.\n\n" +
                            "userId (생략가능, JWT토큰에서 값을 직접 뽑아내며, 값이 주어져도 무시됨)\n\n"+
                            "userName (생략가능, 변경불가항목, 값이 주어져도 무시됨)\n\n"+
                            "email (생략가능, 해당 값을 변경하는 기능은 제공하지 않을 예정, 보낼경우 무시됨)\n\n" +
                            "createAttachmentsId (생략가능, 프로필사진 변경하려면 PK값 입력, 변경 안하려면 변수 선언 X) \n\n" +
                            "accountAuthority (생략가능, 계정 권한 변경은 제공하지 않을 예정. 보낼경우 무시됨. 협의 필요) \n\n" +
                            "status (생략가능, 이 API를 통해 해당 값을 바꿀 수 없음. 보낼 경우 무시됨 협의 필요) \n\n" +
                            "isDeleted (생략가능, 이 API를 통해 해당 값을 바꿀 수 없음. 보낼 경우 무시됨 협의 필요) \n\n"
            )
            @RequestBody UserRequestDto userRequestDto
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        HashMap<String, Object> resultMap = userService.modifyUser(securityUser.getUser().getUserId(), userRequestDto);
        response.setResult(resultMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200", description = "성공시 result에 key:attachmentsId 의 value로 업로드 성공한 첨부파일 id 제공")
    @Operation(summary = "사용자 프로필 사진(프로필) 업로드", description = "사용자 프로필 사진(프로필) 업로드, JWT 토큰 필요")
    public ResponseEntity<?> uploadUserAttachments(
            HttpServletRequest request,
            @RequestPart(required = true) MultipartFile attachments
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse restResponse = new RestResponse();

        HashMap<String, Object> resultMap = userService.uploadUserAttachments(securityUser, attachments);
        restResponse.setResult(resultMap);
        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

}
