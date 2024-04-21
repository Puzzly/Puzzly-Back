package com.puzzly.api.entity;

import com.puzzly.api.domain.AccountAuthority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
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
    @Column private boolean gender;
    @Enumerated(EnumType.STRING) private AccountAuthority accountAuthority;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime deleteDateTime;
    @Column private String status;

    // 사용자 추가정보
    @OneToOne(mappedBy="user")
    private UserEx userEx;

    // 소속 켈린더 정보
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
    private List<UserCalSyncs> syncList = new ArrayList<>();







    /*
    @OneToMany(mappedBy="calendarId")
    private List<Calendar> calendarList = new ArrayList<>();

     */
/*
    public User(UserRequestDto userDto, UserExRequestDto userExDto){

        this.userId = userDto.getUserId();
        this.userName=userDto.getUserName();
        this.nickName=userDto.getNickName();
        this.email=userDto.getEmail();
        this.password=userDto.getPassword();
        this.phoneNumber=userDto.getPhoneNumber();
        this.birth=userDto.getBirth();
        this.gender=userDto.isGender();
        this.accountAuthority=userDto.getAccountAuthority();
        this.createDateTime=userDto.getCreateDateTime();
        this.modifyDateTime=userDto.getModifyDateTime();
        this.deleteDateTime=userDto.getDeleteDateTime();
        this.status=userDto.getStatus();
        this.userEx = new UserEx(userExDto);
    }*/

}
