package com.puzzly.api.entity;

import com.puzzly.api.dto.request.UserExRequestDto;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.enums.AccountAuthority;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
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

    // NOTE @OneToOne 상황에서 Lazy Loading은 주인쪽에서만 발동한다.
    // 종속쪽에서 부르면 EAGER 로딩으로 동작한다.
    // REF : https://velog.io/@moonyoung/JPA-OneToOne-%EC%96%91%EB%B0%A9%ED%96%A5-%EB%A7%A4%ED%95%91%EA%B3%BC-LazyLoading

    @OneToOne(mappedBy="user", cascade = CascadeType.PERSIST)
    @PrimaryKeyJoinColumn
    private UserEx userEx;

    @OneToMany(mappedBy="calSyncId")
    private List<UserCalSyncs> syncList = new ArrayList<>();

    @OneToMany(mappedBy="diaryId")
    private List<Diary> diaryList = new ArrayList<>();

    @OneToMany(mappedBy="checkListId")
    private List<CheckList> checklistList = new ArrayList<>();

    @OneToMany(mappedBy="calendarId")
    private List<Calendar> calendarList = new ArrayList<>();

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
    }

}
