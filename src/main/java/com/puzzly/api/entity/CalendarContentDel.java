package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="calendar_content_del")
public class CalendarContentDel {
    @Comment("PK, autoIncrement")
    @Id
    private Long contentId;
    @Comment("일정 시작시각")
    @Column private LocalDateTime startDateTime;
    @Comment("일정 종료시각")
    @Column private LocalDateTime endDateTime;

    @Comment("일정 제목")
    @Column private String title;
    @Comment("일정 위치")
    @Column private String location;
    @Comment("일정 종류")
    @Column private String type;
    @Comment("일정 내용")
    @Column(columnDefinition = "text")
    private String content;
    @Comment("알림 여부")
    @Column private Boolean notify;

    // 알림은 여러개일 수 있으므로 별도 테이블로 관리 해야함.
    //@Column private LocalDateTime notifyTime;
    @Comment("메모")
    @Column private String memo;
    /** Schedule로 삭제 될 대상인지를 체크하는 필드, softDelete*/
    @Comment("삭제여부")
    @Column private Boolean isDeleted;

    @Comment("소속 캘린더")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendarId", referencedColumnName = "calendarId", nullable=false)
    private Calendar calendar;

    @Comment("일정 생성자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "createId", nullable = true)
    private User createUser;
    @Comment("일정 수정자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "modifyId", nullable = true)
    private User modifyUser;
    @Comment("일정 삭제자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "deleteId", nullable = true)
    private User deleteUser;
    @Comment("일정 생성시각")
    @Column private LocalDateTime createDateTime;
    @Comment("일정 수정시각")
    @Column private LocalDateTime modifyDateTime;
    @Comment("일정 삭제시각")
    @Column private LocalDateTime deleteDateTime;
}
