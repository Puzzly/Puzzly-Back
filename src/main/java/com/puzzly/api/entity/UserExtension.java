package com.puzzly.api.entity;

import com.puzzly.api.domain.JoinType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Comment("사용자 확장정보")
@Table(name="user_extension")
public class UserExtension {
    @Comment("PK, autoIncrement")
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long extensionId;
    @Comment("가입종류")
    @Enumerated(EnumType.STRING) private JoinType joinType;
    @Comment("사용자 상태메시지")
    @Column private String statusMessage;
    @Comment("사용자 프로필 경로")
    @Column private String profilePath;
    @Comment("사용자 프로필 확장자")
    @Column private String extension;
    @Comment("사용자 프로필 이름")
    @Column private String originName;
    @Comment("사용자 프로필 파일크기")
    @Column private Long fileSize;
    @Comment("약관1 동의여부")
    @Column Boolean firstTermAgreement;
    @Comment("약관2 동의여부")
    @Column Boolean secondTermAgreement;

    @Comment("사용자 개인화 정보")
    //@Column(columnDefinition="longText")
    @JdbcTypeCode(SqlTypes.JSON)
    private String personalSetting;

}
