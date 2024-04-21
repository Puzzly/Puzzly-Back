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
@Table(name="tb_calendar_contents_del")
public class CalendarContentsDel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long contentsId;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "createId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JoinColumn(name = "createId", nullable = false)
    private User user;

    @Column private LocalDateTime startLocalDateTime;
    @Column private LocalDateTime endLocalDateTime;
    @Column private String location;
    @Column private String type;
    @Lob private String contents;
    @Column private boolean notify;
    @Column private int notifyMin;
    @Column private String memo;

    // 소속된 마더 켈린더
    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "calendarId", referencedColumnName = "calendarId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //@JoinColumn(name = "calendarId", referencedColumnName = "calendarId", nullable=false)
    private Calendar calendar;

    // 켈린더 라벨
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="labelId", referencedColumnName = "labelId", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //@JoinColumn(name="labelId", referencedColumnName = "labelId")
    private CalendarLabel calendarLabel;

    // 켈린더 첨부파일
    // 서비스레벨에서 첨부파일 삭제할것이니까, detach
    // tb_del 은 tb 에서 옮겨지고, schedule로 삭제될것임.
    // 이에따라 tb는 del 정보를 알 필요도, 알 수도 없음. 논리제어로 첨부파일 제거 필요
/*    @OneToMany(mappedBy = "calendarContents")
    private List<CalenderAttachments> CalendarattachmentsList= new ArrayList<>();*/
}
