package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name="calendar_content_user_relation")
@Builder
public class CalendarContentUserRelation {
    @Comment("PK, autoIncrement")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationId;

    @Comment("대상 캘린더 컨텐트")
    @ManyToOne(fetch = FetchType.LAZY ) @JoinColumn(name = "contentId")
    private CalendarContent calendarContent;

    @Comment("참가 사용자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "userId")
    private User user;

    @Comment("삭제 여부")
    private Boolean isDeleted;
}
