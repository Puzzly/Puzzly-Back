package com.puzzly.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Table(name="user_attachments")
public class UserAttachments {
    @Comment("PK, autoIncrement")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentsId;
    @Comment("소속 사용자")
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "userId", nullable = true)
    private User user;
    @Comment("첨부파일 확장자")
    @Column private String extension;
    @Comment("원본 이름")
    @Column private String originName;
    @Comment("서버상 절대경로")
    @Column private String filePath;
    @Comment("파일 크기")
    @Column private Long fileSize;


    /** schedule로 삭제 될 대상인지를 체크하는 필드, softDelete*/
    @Comment("삭제여부")
    @Column private Boolean isDeleted;
    @Comment("첨부파일 생성자")
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="createId", referencedColumnName = "userId")
    private User createUser;
    @Comment("첨부파일 삭제자")
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="deleteId", nullable = true)
    private User deleteUser;
    @Comment("첨부파일 생성시각")
    @Column private LocalDateTime createDateTime;
    @Comment("첨부파일 삭제시각")
    @Column private LocalDateTime deleteDateTime;
}
