package com.puzzly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="tb_calendar")
public class PuzzlyCal {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long no;

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
