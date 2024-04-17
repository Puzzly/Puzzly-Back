package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Table(name="tb_private_calendar")
public class _PrivateCalender_Deprecated {

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
