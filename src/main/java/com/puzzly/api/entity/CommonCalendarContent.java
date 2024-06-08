package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Comment("공공API 달력")
@Table(name="common_calendar_content")
public class CommonCalendarContent {
    @Comment("PK, autoIncrement")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Comment("일정 시작시각")
    @Column private LocalDateTime startDateTime;

    @Comment("일정 종료시각")
    @Column private LocalDateTime endDateTime;

    @Comment("일정 제목")
    @Column private String title;

    @Comment("일정 종류")
    @Column private String type;

    @Comment("휴일 여부")
    @Column private Boolean isHoliday;
}
