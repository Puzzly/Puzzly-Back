package com.puzzly.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자에 관련된 답변을 전달할 때 사용하는 DTO")

public class UserResponseDto {
    /** Depends On User.java */
    @Schema(description = "사용자 PK", defaultValue = "1")
    private Long userId;
    @Schema(description = "사용자 ID")
    private String memberId;
    @Schema(description = "사용자 이름", defaultValue = "김미영")
    private String userName;
    @Schema(description = "사용자 별명", defaultValue = "김퍼즐리")
    private String nickName;
    @Schema(description = "사용자 전화번호", defaultValue = "010-1111-2222")
    private String phoneNumber;
    @Schema(pattern = "2024-04-21", type="string", description = "사용자 생년월일")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    @Schema(description = "사용자 성별: 1,3(남) / 2,4 (여)", defaultValue = "1")
    private Integer gender;
    @Schema(description = "사용자 email, unique", defaultValue = "puzzly@puzzly.com")
    private String email;
    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "생성일자, 대체로 무시해도 됌. 서버에서 생성해서 사용")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;
    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "수정일자, 대체로 무시해도 됌. 서버에서 생성해서 사용")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDateTime;
    @Schema(pattern = "2024-04-21 21:37:00", type="string", description = "삭제일자, 대체로 무시해도 됌. 서버에서 생성해서 사용")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDateTime;
    @Schema(description="사용자 탈퇴여부", defaultValue = "false")
    private Boolean isDeleted;

    /** Depends On UserExtension.java*/
    /*
    @Schema(defaultValue = "1", description = "사용자 추가정보 PK")
    private Long extensionId;
     */
    @Schema(description="가입종류(Native/KAKAO/NAVER..etc)")
    private String joinType;
    @Schema(defaultValue = "WELCOME PUZZLY", description = "사용자 상태메시지")
    private String statusMessage;
    @Schema(defaultValue = "", description="사용자 프로필 경로")
    private String profilePath;
    @Schema(defaultValue = "", description="사용자 프로필 확장자")
    private String extension;
    @Schema(defaultValue = "", description="사용자 프로필 이름")
    private String originName;
    @Schema(defaultValue = "", description="사용자 프로필 파일크기")
    private Long fileSize;
    @Schema(defaultValue = "true", description = "약관 1 동의여부")
    private Boolean firstTermAgreement;
    @Schema(defaultValue = "true", description = "약관 2 동의여부")
    private Boolean secondTermAgreement;
    @Schema(defaultValue = "", description = "사용자 개인화 정보")
    private String personalSetting;

    /** Depends On accountAuthority.java*/
    @Schema(description = "사용자 계정 권한, ROLE_ADMIN / ROLE_USER / 단, ROLE_ADMIN이 주어지면 자동으로 ROLE_USER가 추가됨", defaultValue = "ROLE_USER")
    private List<String> accountAuthority;

    // JSON Format 쓰기 전엔 아래 코드로 대응했음
    // getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    // Json으로 올려받고 내려줄때 패턴 선언, String 까지 써야 Swagger가 알아들음
    // REFER : https://nelljundev.tistory.com/217

    /*
    @Schema(defaultValue = "CREATE", description = "계정상태. CREATE: 정상 / DELETED : 탈퇴")
    private String status;
    */

    // 사용자 추가정보

/*
    @Schema(defaultValue = "", description = "사용자 첨부파일(프로필) 정보")
    private UserAttachmentsResponseDto userAttachments;
 */
}
