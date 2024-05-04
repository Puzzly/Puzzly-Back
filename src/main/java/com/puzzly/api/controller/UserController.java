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
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "01. User", description = "사용자 관련 Operation\n\n 사용 DTO : UserRequestDto, UserResponseDto\n\n DTO 설명은 이 페이지 최 하단부 schema 설명 참조")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    @Operation(summary = "회원가입, 로그인 및 JWT 토큰 필요 X", description = "회원가입, 토큰필요 X")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> createUser(
            HttpServletRequest request,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능하며, 서버에서 default value를 생성함\n\n" +
                            "userId (생략가능, JWT토큰에서 값을 직접 뽑아내며, 값이 주어져도 무시됨)\n\n"+
                            "accountAuthority (생략가능, defaultValue = ROLE_USER, 가능한 다른 값 : ROLE_ADMIN \n\n" +
                            "status (생략가능, defaultValue = CREATE) \n\n" +
                            "isDeleted (생략가능, defaultValue = false) \n\n" +
                            "createDateTime / ModifyDateTime (생략가능, 서버에서 자체적으로 생성함)"+
                            "statusMeesage (생략가능, 이 API에서는 오히려 이 값이 주어지면 무시됨) \n\n" +
                            "profileFilePath (생략가능, 이 API에서는 오히려 이 값이 주어지면 무시됨) \n\n" +
                            "* 상기 명시되지 않은 파라미터가 생략되면 400에러 발생"
            )
            @RequestBody UserRequestDto userRequestDto
            ) throws FailException {
        RestResponse response = new RestResponse();
        UserResponseDto user = userService.insertUser(userRequestDto);
        response.setResult(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    @Operation(summary = "내 정보 조회, JWT 토큰필요 O", description = "내 정보 조회, 토큰 필요 O")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> getUserMybatis(
            HttpServletRequest request
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponseDto user = userService.selectUserMybatis(securityUser.getUser().getUserId());
        RestResponse response = new RestResponse();
        response.setResult(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping()
    @Operation(summary = "내 정보 변경하기 (프로필사진 업로드 기능은 추후개발 (5월 3주차)", description = "내 정보 수정, 토큰 필요 O")
    public ResponseEntity<?> updateUser(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description="이 API에서 아래의 값은 생략이 가능하며 변경사항이 발생하지 않은 값은 전송하지 않아도 됌.\n\n" +
                            "userId (생략가능, JWT토큰에서 값을 직접 뽑아내며, 값이 주어져도 무시됨)\n\n"+
                            "email (생략가능, 해당 값을 변경하는 기능은 제공하지 않을 예정, 보낼경우 무시됨)\n\n" +
                            "accountAuthority (생략가능, 계정 권한 변경은 제공하지 않을 예정. 보낼경우 무시됨. 협의 필요) \n\n" +
                            "status (생략가능, 이 API를 통해 해당 값을 바꿀 수 없음. 보낼 경우 무시됨 협의 필요) \n\n" +
                            "isDeleted (생략가능, 이 API를 통해 해당 값을 바꿀 수 없음. 보낼 경우 무시됨 협의 필요) \n\n"
            )
            @RequestBody UserRequestDto userRequestDto
    ) throws FailException{
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RestResponse response = new RestResponse();

        UserResponseDto user = userService.updateUser(securityUser.getUser().getUserId(), userRequestDto);
        response.setResult(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Deprecated
    @GetMapping("/jpa")
    @Operation(summary = "@Deprecated 내 정보 조회", description = "@Deprecated. 내 정보 조회 ")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    @ApiResponse(responseCode = "4**", description = "SERVER_MESSAGE_* 메시지는 의도된 예외처리")
    public ResponseEntity<?> getUser(
            HttpServletRequest request
    ){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //UserResponseDto user = userService.selectUser(securityUser.getUser().getUserId());

        RestResponse response = new RestResponse();
        //response.setResult(user);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
