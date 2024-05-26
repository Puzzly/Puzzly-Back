package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Comment("일정 반복 정보")
@Table(name="calendar_content_recurring_info")
public class CalendarContentRecurringInfo {

    @Comment("PK, autoIncrement")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long infoId;

    @Comment("대상 캘린더 컨텐트")
    @OneToOne(fetch = FetchType.LAZY)
    private CalendarContent calendarContent;

    @Comment("반복 종류 : 매일(D), 매주(W), 매월(M), 매년(Y)")
    @Column
    private String recurringType;

    // recurrying Type : period
    // D               : 2  = 매 4일마다
    // W               : 2  = 매 2주마다 + date
    //                                 + [0,1,4]   -> 매 2주마다 0(일), 1(월), 4(목)
    // M               : 0  = 매달 + day
    //                            + 4        = 매달 4일에 반복
    // M               : 3  = 매 월 3째주 + date
    //                                  + 0  = 매 월 3째주 일요일에
    // Y               : 매년
    @Comment("반복 주기")
    @Column private Integer period;
    @Comment("반복 요일 구분자 , ")
    @Column private String recurringDate;

    @Comment("반복할 일자")
    @Column private Integer recurringDay;

    @Comment("반복 횟수 종료조건")
    @Column private Long conditionCount;
    @Comment("현재까지 반복 횟수")
    @Column private Long currentCount;
    @Comment("반복 날짜 종료 조건")
    @Column private LocalDate conditionEndDate;

    @Comment("삭제 여부")
    @Column private Boolean isDeleted;
}
