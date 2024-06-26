package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentCommentResponseDto;
import com.puzzly.api.dto.response.CalendarLabelResponseDto;
import com.puzzly.api.entity.QCalendarContent;
import com.puzzly.api.entity.QCalendarContentAttachments;
import com.puzzly.api.entity.QCalendarContentComment;
import com.puzzly.api.entity.QCalendarLabel;
import com.puzzly.api.entity.QUser;
import com.puzzly.api.entity.QUserAttachments;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

@RequiredArgsConstructor
public class CalendarContentCommentJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public List<CalendarContentCommentResponseDto> selectCalendarContentCommentList(Long contentId, Long beforeCommentId, int pageSize) {
        QCalendarContentComment calendarContentComment = QCalendarContentComment.calendarContentComment;
        QUserAttachments userAttachments = QUserAttachments.userAttachments;
        QUser user = QUser.user;

        return jpaQueryFactory
                .select(Projections.fields(CalendarContentCommentResponseDto.class,
                    calendarContentComment.commentId,
                    calendarContentComment.comment,
                    user.userId.as("createId"),
                    user.nickName.as("createNickName"),
                    userAttachments.filePath,
                    calendarContentComment.createDateTime
                ))
                .from(calendarContentComment)
                    .leftJoin(user).on(calendarContentComment.createUser.userId.eq(user.userId))
                    .leftJoin(userAttachments).on(userAttachments.user.userId.eq(user.userId))
                .where(calendarContentComment.calendarContent.contentId.eq(contentId),
                       calendarContentComment.commentId.lt(beforeCommentId))
                .orderBy(calendarContentComment.createDateTime.desc())
                .limit(pageSize)
                .fetch();
    }



}
