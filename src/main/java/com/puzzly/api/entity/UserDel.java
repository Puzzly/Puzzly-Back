package com.puzzly.api.entity;

import com.puzzly.api.domain.AccountAuthority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Comment("삭제 사용자 테이블")
/* User가 예약어 회피를 위해 Users로 명명, 이에따라 del 테이블도 Users로 명명*/
@Table(name="users_del")
public class UserDel {

    @Comment("Pk, autoIncrement")
    @Id
    private Long userId;
    @Comment("사용자 이름")
    @Column private String userName;
    @Comment("사용자 별명")
    @Column private String nickName;
    @Comment("사용자 email")
    @Column(unique = true) private String email;
    @Comment("사용자 비밀번호")
    @Column private String password;
    @Comment("사용자 전화번호")
    @Column private String phoneNumber;
    @Comment("사용자 생일")
    @Column private LocalDate birth;
    @Comment("사용자 성별")
    @Column private Boolean gender;
    @Comment("사용자 생성시각")
    @Column private LocalDateTime createDateTime;
    @Comment("사용자 수정시각")
    @Column private LocalDateTime modifyDateTime;
    @Comment("사용자 삭제시각")
    @Column private LocalDateTime deleteDateTime;
    @Comment("사용자 계정상태")
    @Column private String status;
    @Comment("사용자 삭제여부")
    @Column private Boolean isDeleted;

    @Comment("사용자 확장정보")
    @Column
    private Long extensionId;
}
