package com.puzzly.api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_calendars_del")
public class CalendarDel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long calendarId;
    @Column private String calendarType;
    @Column private String calendarName;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;

    // 소유주
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //@JoinColumn(name = "ownerId", referencedColumnName = "userId", nullable = false)
    private User user;

    // 켈린더가 지워진다면, 관계, 컨텐츠 둘다 지워져야하는데
    // tb_del 정보는 tb 에서 옮겨져옴. 이에따라 아래 정보들은 논리제어로 정리해야함

    /*
    // 그룹관계정일
    @OneToMany(mappedBy="calendar")
    private List<CalendarUserRel> calendarUserRelList = new ArrayList<>();

    // 캘린더 하위 컨텐츠 정보임
    // 논리제어할것임
    @OneToMany(mappedBy = "calendar")
    private List<CalendarContents> calendarContentsList= new ArrayList<>();

     */
}
