package com.puzzly.api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.puzzly.api.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_diarys")
public class Diary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long diaryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;
    @Lob private String contents;
    // Create Time 자체가 "작성일, == Target Date" 로 동작 (다이어리, 일기장)
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime deleteDateTime;
    // NOTE one to many <> many to one 선언 및 cascade, FK 제약조건 거는거 찾아봐야한다.
    // cascade 옵션이 있다. remove 는 delete까지 전파, persist는 insert, all은 싹 다.
    @OneToMany(mappedBy = "attachmentId")
    private List<DiaryAttachments> attachments= new ArrayList<>();

}
