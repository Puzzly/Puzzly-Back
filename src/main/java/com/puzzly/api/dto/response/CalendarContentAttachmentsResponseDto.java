package com.puzzly.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.puzzly.api.entity.CalendarContent;
import com.puzzly.api.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarContentAttachmentsResponseDto {

    private Long attachmentsId;

    private Long calendarContentId;
    private String extension;
    private String originName;
    private String filePath;
    private Long fileSize;

    private Boolean isDeleted;

    private Long createId;
    private String createNickName;

    private Long deleteId;
    private String deleteNickName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteDateTime;
}
