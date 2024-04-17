package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_calendar_labels")
public class CalendarLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long labelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column private String contents;
}
