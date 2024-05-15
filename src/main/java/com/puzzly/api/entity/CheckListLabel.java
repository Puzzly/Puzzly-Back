package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
@Table(name="tb_checklist_labels")
public class CheckListLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long labelId;

    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "createId", nullable=false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JoinColumn(name = "createId", nullable=false)
    private User user;

    @Column private String content;
    @OneToMany(mappedBy="checklistLabel")
    private List<CheckList> checkListList = new ArrayList<>();
}
