package com.puzzly.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "캘린더 일정 댓글에 관련된 응답을 전달할 때 사용하는 DTO")
public class CalendarContentCommentResponseDto {

    @Schema(description = "comment PK")
    private Long commentId;

    @Schema(description = "댓글", defaultValue = "댓글입니다.")
    private String comment;

    @Schema(description = "댓글 작성자 PK", defaultValue = "1")
    private long createId;
    @Schema(description = "댓글 작성자 닉네임", defaultValue = "김퍼즐리")
    private String createNickName;
    @Schema(description = "댓글 작성자 프로필 사진 서버상 절대경로")
    private String filePath;

    @Schema(description = "댓글 생성 시각", pattern = "2024-04-22 00:00:00", type="string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;
}
