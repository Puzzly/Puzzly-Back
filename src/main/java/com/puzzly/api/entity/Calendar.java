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
@Table(name="tb_calendars")
public class Calendar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long calendarId;

    /** need to refactor. > 달력 type 없음*/
    @Column private String calendarType;
    @Column private String calendarName;
    @Column private LocalDateTime createDateTime;
    @Column private LocalDateTime modifyDateTime;
    @Column private LocalDateTime DeleteDateTime;

    /** Note FK 관계설정
     * REF:// mjmjmj98.tistory.com/m/152
     *
     * ManyToOne, OneToMany 등으로 관계를 매핑한다.
     * 이때, 고려해야 할 사항은 아래와 같다.
     * N:1 관계에서는 주로 외래키는 N 쪽에 있다. 이를 N(다) 쪽이 연관관계의 주인이라 표현한다.
     * 주인이 아닌쪽 ( 이 주석에서는 종속 ) 에서는 외래키 변경이 불가능하며 읽기만 가능하다.
     * 주인이 아닌쪽은 mappedBy 를 사용하여 주인필드 이름값을 입력한다.
     *
     * 1. 연관관계의 주인 쪽에서 @ManyToOne @JoinColumn(name = "{{FK로 관리될 Column의 이름}}") 을 선언한다.
     * 2. @ManyToOne 걸은 @Column은 그 Entity 객체를 멤버로 선언한다.
     *
     * 상기 1,2 만으로 FK N:1 "단방향" 관계 성립을 마친다.
     * 일 례로 팀원 - 팀  N:1 관계를 매핑하면,
     * 팀원에 @ManyToOne @JoinColumn(name="team_id") Team team
     * 팀에는 @Id 어노테이션을 가진 team_id 멤버변수만 선언되어있으면 된다.
     * 이때, 팀원은 어느팀에 속해있는지 알 수 있지만 팀은 어느팀원을 가졌는지 알 수 없다.
     *
     * 상기 1,2로 처리할 수 없는 N:1 "양방향" 관계 성립은 아래와같이 수행한다.
     * 3. 연관관계의 주인쪽에서 @ManyToOne @JoinColumn(name = "{{FK로 관리될 Column의 이름}}") 을 선언한다.
     * 4. @ManyToOne 걸은 @Column은 그 Entity 객체를 멤버로 선언한다.
     * 5. 연관관계의 종속 쪽에서 @OneToMany(mappedBy = "{{주인측 @ManyToOne걸은 @Column의 멤버변수명}}")를 선언하고
     * 6. List<N 역할의 클래스> 변수명 = ArrayList 를 선언한다.
     * 이렇게 하면 "팀원이 어느팀에 속해있는지 알 수 있고, 팀이 어느 팀원을 가졌는지 알 수 있다"
     *
     * 그렇다면 1:N은 어떻게 구성할까?
     * 특이점은, N 테이블이 FK를 관리하지 않고 (멤버테이블에서 팀Id를 가지지 않고)
     * 팀이 멤버ID를 가진다.
     * 아래 방법이 1:N 단방향 구성
     * 즉, Team에 @OneToMany(mappedBy="team") @JoinColumn(name="team_id") List<Member> members = new ArrayList<>
     *     팀멤버에 X 가 되는것.
     *     이때 문서는 1쪽에서 관리하는 FK가 N쪽의 테이블에 있다는점이 단점이라는데 왜인지는 모르겠다..
     *     ^ 아마도 일반적으로 db 짤 때, 정규화로 한개 row가 한개값 가지도록? 혹은 1 쪽이 row가 많이생겨서? 인거같음
     *     다만, 엔티티 저장과 연관관계 처리를 위해 insert 이외에 추가로 update SQL이 수행되어야 한다고 한다.
     *
     * 1:N 양방향 설정의 경우, N쪽에서 FK가 관리되어야 하므로, 양방향 매핑에서 @OneToMany는 주인일 수 없음.
     * 다만 일대다 단방향 매핑 반대편에 단방향 매핑을 읽기전용으로 만들어서 처리는 가능하다.
     * 아래의 경우 1쪽이 주인인것.
     * Team에서 @OneToMany(mappedBy="team") @JoinColumn(name="team_id") list<Member> = ArrayList
     *   ^ mappedBy 썼으므로 주인이아님
     * Team멤버에서 @ManyToOne @JoinColumn(name="TEAM_ID", insertable=false, updatable=false) Team
     *   ^ JoinColumn 선언만 있으므로 주인
     * */

    // 소유주
    // TODO 실 개발모드에서는 fk 제약 해제할것
    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "ownerId", referencedColumnName = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JoinColumn(name = "ownerId", referencedColumnName = "userId", nullable = false)
    private User user;

    // 그룹관계정일
    @OneToMany(mappedBy="calendar")
    private List<CalendarUserRel> userRequestDtoUserRelList = new ArrayList<>();

    // 캘린더 하위 컨텐츠 정보
    // 논리제어할것임
    @OneToMany(mappedBy = "calendar")
    private List<CalendarContents> calendarContentsList= new ArrayList<>();

    @OneToMany(mappedBy = "calendar")
    private List<CalendarLabelCalendarRel> calendarLabelCalendarRelList = new ArrayList<>();

}
