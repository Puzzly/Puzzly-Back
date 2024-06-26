package com.puzzly.api.entity;

import com.puzzly.api.enums.AlarmType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Comment("일정 댓글")
@Table(name="calendar_content_comment")
public class CalendarContentComment {

    @Comment("PK, autoIncrement")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Comment("소속 캘린더 일정")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentId", referencedColumnName = "contentId", nullable=false)
    private CalendarContent calendarContent;
    @Comment("댓글 작성자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User createUser;
    @Comment("댓글")
    @Column private String comment;
    @Comment("댓글 작성일")
    @Column private LocalDateTime createDateTime;
    @Comment("댓글 시스템 여부")
    @Column private Boolean isSystem;


}
