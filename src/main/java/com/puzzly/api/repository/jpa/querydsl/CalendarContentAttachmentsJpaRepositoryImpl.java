package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentAttachmentsResponseDto;
import com.puzzly.api.entity.QCalendarContentAttachments;
import com.puzzly.api.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CalendarContentAttachmentsJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public List<CalendarContentAttachmentsResponseDto> selectCalendarContentAttachmentsByContentId(Long contentId, Boolean isDeleted){
        QCalendarContentAttachments qcca = new QCalendarContentAttachments("qcca");
        QUser user = QUser.user;
        return jpaQueryFactory
                .select(Projections.fields(CalendarContentAttachmentsResponseDto.class,
                    qcca.attachmentsId, qcca.calendarContent.contentId, qcca.extension, qcca.originName,
                    qcca.filePath, qcca.fileSize,
                    user.userId.as("createId"),
                    user.nickName.as("createNickName"),
                    qcca.createDateTime))
                .from(qcca)
                .leftJoin(user).on(qcca.createUser.userId.eq(user.userId))
                .where(qcca.isDeleted.eq(isDeleted))
                .fetch();
    }
}

