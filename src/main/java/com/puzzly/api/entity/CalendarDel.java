package com.puzzly.api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="calendar_del")
public class CalendarDel {

    @Comment("PK, autoIncrement")
    @Id
    private long calendarId;

    /** 캘린더에 한명만 참여하고있으면 PRIVATE, 두명 이상이 참여중이면 TEAM 값을 가짐*/
    @Comment("캘린더 종류")
    @Column private String calendarType;
    @Comment("캘린더 이름")
    @Column private String calendarName;

    /** Schedule로 삭제 될 대상인지를 체크하는 필드, softDelete */
    @Comment("캘린더 삭제여부")
    @Column private Boolean isDeleted;
    @Comment("캘린더 생성자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "createId", referencedColumnName = "userId")
    private User createUser;
    @Comment("캘린더 수정자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "modifyId", referencedColumnName = "userId")
    private User modifyUser;
    @Comment("캘린더 삭제자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "deleteId", referencedColumnName = "userId")
    private User deleteUser;
    @Comment("캘린더 생성시각")
    @Column private LocalDateTime createDateTime;
    @Comment("캘린더 수정시각")
    @Column private LocalDateTime modifyDateTime;
    @Comment("캘린더 삭제시각")
    @Column private LocalDateTime deleteDateTime;

}
