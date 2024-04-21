package com.puzzly.api.entity;

import com.puzzly.api.domain.AccountAuthority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_users_del")
public class UserDel {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    @Column private String userName;
    @Column private String nickName;
    @Column private String email;
    @Column private String password;
    @Column private String phoneNumber;
    @Column private LocalDate birth;
    @Column private boolean gender;
    @Enumerated(EnumType.STRING) private AccountAuthority accountAuthority;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime deleteDateTime;
    @Column private String status;

    // del 테이블은 생성시점에 제어할것이며, 팀 캘린더의 경우 지우면 안됨.
    // 또한 아래 정보들은 생성시점에서 tb테이블이 알 필요 없고 (del정보임), 이에따라 엔티티도 몰라야함.
    // 논리제어 필요

/*    // 사용자 추가정보
    @OneToOne(mappedBy="user")
    @PrimaryKeyJoinColumn
    private UserEx userEx;


    */
/*    // 소속 켈린더 정보
    @OneToMany(mappedBy="user")
    private List<CalendarUserRel> calendarUserRelList = new ArrayList<>();

    // 소유한 캘린더 정보
    @OneToMany(mappedBy="user")
    private List<Calendar> calendarList = new ArrayList<>();

    // 내가 쓴 켈린더 컨텐츠
    @OneToMany(mappedBy="user")
    private List<CalendarContents> calendarContentList = new ArrayList<>();

    // 내가 만든 켈린더 라벨 정보
    @OneToMany(mappedBy="user")
    private List<CalendarLabel> calendarLabelList = new ArrayList<>();

    // 내가 만든 켈린더 첨부파일 정보
    @OneToMany(mappedBy = "user")
    private List<CalenderAttachments> calenderAttachmentsList = new ArrayList<>();

    // 체크리스트 정보
    @OneToMany(mappedBy="user")
    private List<CheckList> checklistList = new ArrayList<>();

    // 체크리스트, 다이어리는 본인소유이므로 본인이 업로드한 첨부파일 목록 양방향 X 처리
    // 필요하다면 mybatis로 처리
    @OneToMany(mappedBy="user")
    private List<Diary> diaryList = new ArrayList<>();

    @OneToMany(mappedBy="user")
    private List<UserCalSyncs> syncList = new ArrayList<>();*/

}
