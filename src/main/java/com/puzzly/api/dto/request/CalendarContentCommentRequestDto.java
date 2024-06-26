

package com.puzzly.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "댓글에 관련된 요청사항을 전달할 떄 사용하는 DTO")
public class CalendarContentCommentRequestDto {
    @Schema(description = "comment PK")
    private Long commentId;

    @Schema(description = "댓글", defaultValue = "댓글입니다.")
    private String comment;
    @Schema(description = "댓글 시스템 여부", defaultValue = "false")
    private Boolean isSystem;

    @Schema(description = "calendar content PK", defaultValue = "1")
    private Long contentId;
}
