package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="calendar_label")
public class CalendarLabel {

    @Comment("PK, autoIncrement")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long labelId;

    @Comment("라벨 이름")
    @Column private String labelName;
    @Comment("라벨 색상")
    @Column private String colorCode;
    @Comment("라벨 순서")
    @Column private Integer orderNum;

    @Comment("라벨 생성자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "createId", referencedColumnName = "userId", nullable=false)
    private User user;

    //라벨이 소속된 캘린더 정보
    @Comment("캘린더 PK")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "calendarId", referencedColumnName = "calendarId", nullable=false)
    private Calendar calendar;
}
