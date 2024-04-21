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
    @JoinColumn(name = "createId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //@JoinColumn(name = "createId", nullable=false)
    private User user;
    @Lob private String contents;
    // Create Time 자체가 "작성일, == Target Date" 로 동작 (다이어리, 일기장)
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime deleteDateTime;
    //  tb_del은 tb 에서 옮겨져오고 스케쥴로 처리될것임.
    // 따라서 아래 정보는 생성시점에 관리될 수 없고 엔티티는 몰라야함. 이에따라 논리제어 필요

/*    @OneToMany(mappedBy = "diary")
    private List<DiaryAttachments> attachments= new ArrayList<>();*/


}
