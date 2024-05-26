package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name="calendar_user_relation")
@Builder
public class CalendarUserRelation {

    @Comment("PK, autoIncrement")
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long relationId;

    @Comment("캘린더 팀원 PK")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="userId")
    private User user;

    @Comment("캘린더 PK")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="calendarId")
    private Calendar calendar;

    @Comment("캘린더 권한")
    @Column private int authority;
    @Comment("권한 삭제여부")
    @Column private Boolean isDeleted;

    @Comment("캘린더 합류 시각")
    @Column private LocalDateTime createDateTime;
    @Comment("일정 탈퇴시각")
    @Column private LocalDateTime deleteDateTime;
}
