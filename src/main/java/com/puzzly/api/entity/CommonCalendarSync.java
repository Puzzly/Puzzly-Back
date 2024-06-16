package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@ToString
@Comment("OpenAPI 캘린더 동기화 기록")
@Table(name="common_calendar_sync")
public class CommonCalendarSync {
    @Comment("PK, AutoIncrement")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long syncId;

    @Column
    @Comment("대상 년도")
    private int syncYear;
    @Column
    @Comment("대상 월")
    private int syncMonth;
    @Column
    @Comment("동기화 시각")
    private LocalDateTime syncDateTime;

}
