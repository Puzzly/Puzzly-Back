package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name="tb_group_user_rels")
public class _GroupUserRel_Deprecated {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long groupUserRelId;
    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne
    @JoinColumn(name="groupId")
    private _Group_Deprecated groupDeprecated;
}
