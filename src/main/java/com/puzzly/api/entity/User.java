package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
// Edit 가능하게 별도 setter 선언 필요
@Entity
@Builder
// TODO Class Builder 제거하고 AllArgsConstructor 지워야한다.
// TODO constructor builder로 가야한다.
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@ToString
@Table(name="tb_users")
public class User {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    @Column private String userName;
    @Column private String nickName;
    @Column private String email;
    @Column private String password;
    @Column private String phoneNumber;
    @Column private LocalDate birth;
    @Column private Boolean gender;

    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    private List<UserAccountAuthority> userAccountAuthorityList = new ArrayList<>();

    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime deleteDateTime;
    @Column private String status;
    @Column private Boolean isDeleted;

    // 사용자 추가정보
    @OneToOne(mappedBy="user")
    private UserEx userEx;

}
