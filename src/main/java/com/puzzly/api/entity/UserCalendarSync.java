package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Comment("사용자 타 달력 동기화 정보")
@Table(name="user_calendar_sync")
public class UserCalendarSync {
    @Comment("PK, autoIncrement")
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long syncId;

    @Comment("사용자 PK")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable=false)
    private User user;
    @Comment("최종 동기화 시각")
    @Column private LocalDateTime lastSyncTime;
}
