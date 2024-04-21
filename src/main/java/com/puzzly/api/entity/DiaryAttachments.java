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
@Table(name="tb_diary_attachments")
public class DiaryAttachments {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long attachmentId;

    // 조회시점에서 포스트정보 필요하지않나..
    @ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name = "diaryId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JoinColumn(name = "diaryId", nullable=false)
    private Diary diary;
    @Column private String extension;
    @Column private String originalName;
    @Column private String filePath;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;
}
