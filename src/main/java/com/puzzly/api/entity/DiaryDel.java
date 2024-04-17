package com.puzzly.api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_diarys_del")
public class DiaryDel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long diaryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;
    @Column private String contents;
    // Create Time 자체가 "작성일, == Target Date" 로 동작 (다이어리, 일기장)
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;
    // TODO one to many <> many to one 선언 및 cascade, FK 제약조건 거는거 찾아봐야한다.
    @OneToMany(mappedBy = "attachmentId", cascade = CascadeType.ALL)
    private List<DiaryAttachments> attachments= new ArrayList<>();

}
