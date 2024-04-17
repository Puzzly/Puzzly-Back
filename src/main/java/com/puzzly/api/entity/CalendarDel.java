package com.puzzly.api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_calendars_del")
public class CalendarDel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long calendarId;
    @Column private String calendarType;
    @Column private String calendarName;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

}
