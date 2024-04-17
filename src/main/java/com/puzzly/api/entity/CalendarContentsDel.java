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
@Table(name="tb_calendar_contents_del")
public class CalendarContentsDel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long contentsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column private LocalDateTime startLocalDateTime;
    @Column private LocalDateTime endLocalDateTime;
    @Column private String location;
    @Column private String type;
    @Lob private String contents;
    @Column private boolean notify;
    @Column private int notifyMin;
    @Column private String memo;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="label_id", referencedColumnName = "labelId")
    private CalendarLabel calendarLabel;
    @OneToMany(mappedBy = "attachmentId", cascade = CascadeType.ALL)
    private List<CalenderAttachments> attachments= new ArrayList<>();
}
