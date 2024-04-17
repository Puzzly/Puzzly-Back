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
@Table(name="tb_user_cal_syncs")
public class UserCalSyncs {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long calSyncId;

    // User를 외래키로 지정하는 방법
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;
    @Column private LocalDateTime lastSyncTime;
    @Column private String syncEmail;
}
