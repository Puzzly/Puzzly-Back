package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name="tb_calendar_user_rels")
@Builder
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

    @Column private int authority;
    @Column private Boolean isDeleted;
}
