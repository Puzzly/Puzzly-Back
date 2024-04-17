package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name="tb_groups")
public class _Group_Deprecated {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long groupId;
    @Column private String groupName;
    @OneToMany(mappedBy="group")
    private List<_GroupUserRel_Deprecated> memberList = new ArrayList<>();
}
