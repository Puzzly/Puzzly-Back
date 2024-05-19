package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder

@AllArgsConstructor
@NoArgsConstructor

@Entity
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
    private User createUser;
    @Comment("라벨 수정자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "modifyId", referencedColumnName = "userId", nullable=true)
    private User modifyUser;
    @Comment("라벨 삭제자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "deleteId", referencedColumnName = "userId", nullable=true)
    private User deleteUser;
    @Comment("라벨 생성시각")
    @Column private LocalDateTime createDateTime;
    @Comment("라벨 수정시각")
    @Column private LocalDateTime modifyDateTime;
    @Comment("라벨 삭제시각")
    @Column private LocalDateTime deleteDateTime;

    //라벨이 소속된 캘린더 정보
    @Comment("캘린더 PK")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "calendarId", referencedColumnName = "calendarId", nullable=false)
    private Calendar calendar;
}
