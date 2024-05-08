package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@ToString
@Builder
// TODO Class Builder 제거하고 AllArgsConstructor 지워야한다.
// TODO constructor builder로 가야한다.
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Table(name="tb_calendar_invite_codes")
public class CalendarInviteCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long inviteCodeId;
    @Lob
    private String inviteCode;
    
}