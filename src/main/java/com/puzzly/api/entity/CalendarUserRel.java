package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Table(name="tb_calendar_user_rels")
public class CalendarUserRel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long calendarUserRelId;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne
    @JoinColumn(name="calendarId")
    private Calendar calendar;

    @Column private String authority;
}
