package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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

    @Comment("약관1 동의여부")
    @Column Boolean firstTermAgreement;
    @Comment("약관2 동의여부")
    @Column Boolean secondTermAgreement;
    @Comment("사용자 상태메시지")
    @Column private String statusMessage;


    @Comment("사용자 프로필 사진정보")
    @Column private String profileFilePath;

    /*
    @Comment("사용자 개인화 정보")
    @Column(columnDefinition="longText")
    private String personalSetting;
    */
}
