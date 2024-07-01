package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@ToString
@Comment("사용자 정보")
/** DB 예약어 user를 피하기 위해 사용자테이블만 복수형으로 선언*/
@Table(name="users")
public class User {
    @Comment("Pk, autoIncrement")
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    @Comment("사용자 ID")
    @Column private String memberId;
    @Comment("사용자 이름")
    @Column private String userName;
    @Comment("사용자 별명")
    @Column private String nickName;
    @Comment("사용자 전화번호")
    @Column private String phoneNumber;
    @Comment("사용자 생일")
    @Column private LocalDate birth;
    @Comment("사용자 성별")
    @Column private Integer gender;
    @Comment("사용자 email")
    @Column(unique = true) private String email;
    @Comment("사용자 비밀번호")
    @Column private String password;

    @Comment("사용자 생성시각")
    @Column private LocalDateTime createDateTime;
    @Comment("사용자 수정시각")
    @Column private LocalDateTime modifyDateTime;
    @Comment("사용자 삭제시각")
    @Column private LocalDateTime deleteDateTime;

    @Comment("사용자 삭제여부")
    @Column private Boolean isDeleted;

    @Comment("사용자 확장정보")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extensionId")
    private UserExtension userExtension;
}
