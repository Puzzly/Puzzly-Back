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
@Table(name="tb_checklists_del")
public class CheckListDel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //@JoinColumn(name = "createId", nullable=false)
    private User user;

    @Column private int orderNum;
    @Lob private String contents;
    @Column private String memo;
    @Column private boolean notify;
    @Column private LocalDateTime notifyLocalDateTime;

    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="labelId", referencedColumnName = "labelId", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CheckListLabel checklistLabel;

    @Column private long parentCheckListId;

    // 아래 정보들은 del테이블로 옮겨진 뒤 논리제어로 제거하거나 유지해야함
    // tb_del은 tb에서 옮겨진 뒤 스케쥴로 처리될 것이므로, 생성시점이나 엔티티에서는 해당 정보를 알 필요 없음.
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    //TODO Insertable, Updatable 이 있는데 이거 뭔지 알아봐야할듯
    // 주 / 부 테이블 둘다 FK를 관리하는 상황을 막기 위해서 insertable, updatable 설정을 FALSE로 설정하고 읽기 전용 필드로 사용해서 양방향 매핑처럼 사용하는 방법이다.
    // 출처: https://ict-nroo.tistory.com/125 [개발자의 기록습관:티스토리]
    // Ref : https://jyami.tistory.com/21
    @JoinColumn(name = "parentCheckListId", referencedColumnName = "checklistId", insertable = false, updatable = false)
    private CheckList parentCheckList;

    @OneToMany(mappedBy="parentCheckList", fetch = FetchType.EAGER)
    private List<CheckList> childrenCheckList = new ArrayList<>();

    @OneToMany(mappedBy = "checkList")
    private List<CheckListAttachments> checkListattachmentsList= new ArrayList<>();

     */
}
