package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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
    @ManyToOne @JoinColumn(name="userId")
    private User user;

    @Comment("캘린더 PK")
    @ManyToOne @JoinColumn(name="calendarId")
    private Calendar calendar;

    @Comment("캘린더 권한")
    @Column private int authority;
    @Comment("권한 삭제여부")
    @Column private Boolean isDeleted;
}
