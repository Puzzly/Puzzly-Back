package com.puzzly.api.entity;

import com.puzzly.api.enums.AccountAuthority;
import com.puzzly.api.enums.JoinType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name="tb_users")
public class _User_deprecated {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    @Column private String username;
    @Column private String nickname;
    @Column private String email;
    @Column private String password;
    @Column private String phoneNumber;
    @Column private LocalDate birth;
    @Column private boolean gender;
    @Enumerated(EnumType.STRING) private JoinType joinType;
    @Enumerated(EnumType.STRING) private AccountAuthority accountAuthority;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;
    @OneToMany(mappedBy="user")
    private List<_GroupUserRel_Deprecated> groupList = new ArrayList<>();

}
