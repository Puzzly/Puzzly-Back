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
@Table(name="tb_calendar_contents_attachments")
public class CalenderAttachments {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long attachmentId;

    // 조회시점에서 포스트정보 필요하지않나..
    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "contentsId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JoinColumn(name = "contentsId", nullable=false)
    private CalendarContents calendarContents ;
    @Column private String extension;
    @Column private String originalName;
    @Column private String filePath;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;

    // 첨부파일 생성자 정보
    @ManyToOne(fetch=FetchType.LAZY)
    //@JoinColumn(name="createId", referencedColumnName = "userId", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JoinColumn(name="createId", referencedColumnName = "userId")
    private User user;
}
