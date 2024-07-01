package com.puzzly.api.dto.request;

import com.puzzly.api.domain.AccountAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자에 관련된 요청사항을 전달할 때 사용하는 DTO")
public class UserRequestDto {
    /** Depend on User.java*/
    @Schema(description = "사용자 PK")
    private Long userId;
    @Schema(description = "사용자 ID", defaultValue="puzzly")
    private String memberId;
    @Schema(description = "사용자 이름", defaultValue = "김미영")
    private String userName;
    @Schema(description = "사용자 별명", defaultValue ="김퍼즐리")
    private String nickName;
    @Schema(description = "사용자 휴대전화번호, pattern 검사 별도로 안하고있음.", defaultValue = "010-1111-2222")
    private String phoneNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "생년월일")
    private LocalDate birth;
    @Schema(description = "성별: 1, 3(남) / 2, 4(여)", defaultValue = "1")
    private Integer gender;
    @Schema(description = "사용자 Email, Unique", defaultValue = "user1@naver.com")
    private String email;
    @Schema(description = "사용자 password", defaultValue = "string")
    private String password;

    /** Depend on UserExtension.java*/

    @Schema(defaultValue = "WELCOME PUZZLY", description = "사용자 상태메시지")
    private String statusMessage;
    @Schema(defaultValue = "", description = "사용자 프로필 경로")
    private String profilePath;
    /*
    @Schema(description = "사용자 프로필 확장자")
    private String extension;
    @Schema(description = "사용자 프로필 이름")
    private String originName;
    @Schema(description = "사용자 프로필 파일크기")
    private Long fileSize;

     */
    @Schema(defaultValue = "true", description = "약관 1 동의여부")
    private Boolean firstTermAgreement;
    @Schema(defaultValue = "true", description = "약관 2 동의여부")
    private Boolean secondTermAgreement;
    @Schema(defaultValue = "", description = "사용자 개인화 정보")
    private String personalSetting;

    /** Depends On AccountAuthority.java*/
    @Schema(description = "계정 자체 권한, ROLE_USER : 일반사용자 / ROLE_ADMIN : 관리자, 단 ROLE_ADMIN이 서버로 주어지면 자동으로 ROLE_USER도 추가됨", defaultValue = "ROLE_USER")
    private AccountAuthority accountAuthority;


    // Date는 T, Z 등 불필요한 요소가 swagger-ui에서 안붙어서 별도 명시 X
    //@JsonFormat(pattern = "yyyy-MM-dd")
    // REFER : https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=varkiry05&logNo=221736856257


    //Swagger에 에시로 출력될 패턴
    // https://stackoverflow.com/questions/49379006/what-is-the-correct-way-to-declare-a-date-in-an-openapi-swagger-file/49379235#49379235
    /* RequestDTO 는 해당 내용을 알 필요 없음

    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "생성일자, 대체로 무시해도 됌. 서버에서 생성해서 사용", hidden = true)
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss",)
    // Json으로 올려받고 내려줄때 패턴 선언, String 까지 써야 Swagger가 알아들음
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;

    @Schema(pattern = "yyyy-MM-dd HH:mm:ss", type="string", example="")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDateTime;

    @Schema(pattern = "yyyy-MM-dd HH:mm:ss", type="string", example = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDateTime;
    @Schema(defaultValue = "CREATE", description = "계정상태. CREATE: 정상 / DELETED : 탈퇴")
    private String status;

    @Schema(defaultValue="false", description="softDelete")
    private Boolean isDeleted;
     */
    // 사용자 추가정보
    // 편의성을 위하여 flat 형태로 요구

    // 프로필 사진이 신규로 들어오면 기존것은 삭제하는 방향으로 진행
    /*
    @Schema(defaultValue = "", description = "등록할 사용자 프로필 사진 PK, 변경 안하려면 이 값 제외")
    private Long attachmentsId;

     */
}
