package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name="tb_group_calender")
public class _GroupCalender_Deprecated {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long calenderId;

    @Column
    private String calId;

    @Column
    private String email;

    @Column
    private LocalDateTime createDateTime;

    @Column
    private String creaId;

    @Column
    private LocalDateTime updateDateTime;

    @Column String updateId;
}
