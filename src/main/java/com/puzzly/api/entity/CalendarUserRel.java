package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Table(name="tb_calendar_user_rels")
public class CalendarUserRel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long calendarUserRelId;


    /** NOTE
     * @ManyToMany를 쓰면 PK로만 구성된 테이블이 자동으로 생기는데,
     * 이때 그 테이블에 추가적인 Column을 별도로 넣을 수 없고
     * 쿼리 실행 타임에 예상치 못한 쿼리가 같이 날아갈 수 있음.
     * 이에따라 일대일-다대일 관계 테이블을 별도 생성해서 관리 */
    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne
    @JoinColumn(name="calendarId")
    private Calendar calendar;
    @Column private String authority;
}
