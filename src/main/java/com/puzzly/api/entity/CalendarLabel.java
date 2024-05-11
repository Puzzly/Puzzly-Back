package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_calendar_labels")
public class CalendarLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long labelId;

    @Column private String contents;

    @Column private String colorCode;
    @Column private Integer order;

    // 라벨 생성자
    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "createId", referencedColumnName = "userId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JoinColumn(name = "createId", referencedColumnName = "userId", nullable=false)
    private User user;

    // 라벨에 소속된 캘린더 컨텐츠 목록
    // 논리제어할것임
    @OneToMany(mappedBy="calendarLabel")
    private List<CalendarContents> calendarContentsList = new ArrayList<>();


}
