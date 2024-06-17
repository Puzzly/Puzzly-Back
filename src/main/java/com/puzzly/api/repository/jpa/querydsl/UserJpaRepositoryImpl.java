package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserJpaRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    public UserResponseDto selectUserByEmail(String email, Boolean isDeleted){
        QUser user = QUser.user;
        QUserExtension userExtension = new QUserExtension("userExtension");
        QUserAttachments userAttachments = new QUserAttachments("userAttachments");
        return jpaQueryFactory
                .select(Projections.fields(UserResponseDto.class,
                        user.userId,user.userName, user.nickName,user.email, user.phoneNumber,
                        user.birth, user.gender,
                        /* 스칼라 서브쿼리
                        ExpressionUtils.as( JPAExpressions.select() )*/
                        user.createDateTime,user.modifyDateTime,user.status,
                        userExtension.extensionId,userExtension.firstTermAgreement,
                        userExtension.secondTermAgreement,userExtension.statusMessage,
                        userAttachments.attachmentsId, userAttachments.extension,
                        userAttachments.originName,userAttachments.filePath,
                        userAttachments.fileSize
                        ))
                .from(user)
                    .leftJoin(userExtension).on(user.userExtension.extensionId.eq(userExtension.extensionId))
                    .leftJoin(userAttachments).on(user.userId.eq(userAttachments.user.userId))
                .where(user.email.eq(email), user.isDeleted.eq(isDeleted)).fetchOne();
    }

    public Boolean selectUserExistsByEmail(String email){
        QUser user = QUser.user;

        return jpaQueryFactory
                .selectFrom(user)
                .where(user.email.eq(email))
                .fetchFirst() != null;
    }

    public Boolean selectUserExistsByEmailAndIsDeleted(String email, Boolean isDeleted){
        QUser user = QUser.user;

        return jpaQueryFactory
                .selectFrom(user)
                .where(user.email.eq(email), user.isDeleted.eq(isDeleted))
                .fetchFirst() != null;
    }

    public UserResponseDto selectUserByUserId(Long userId, Boolean isDeleted){
        QUser user = QUser.user;
        QUserExtension userExtension = new QUserExtension("userExtension");
        QUserAttachments userAttachments = new QUserAttachments("userAttachments");
        return jpaQueryFactory
                .select(Projections.fields(UserResponseDto.class,
                        user.userId,user.userName, user.nickName,user.email, user.phoneNumber,
                        user.birth, user.gender,
                        /* 스칼라 서브쿼리
                        ExpressionUtils.as( JPAExpressions.select() )*/
                        user.createDateTime,user.modifyDateTime,user.status,
                        userExtension.extensionId,userExtension.firstTermAgreement,
                        userExtension.secondTermAgreement,userExtension.statusMessage,
                        userAttachments.attachmentsId, userAttachments.extension,
                        userAttachments.originName,userAttachments.filePath,
                        userAttachments.fileSize
                ))
                .from(user)
                .leftJoin(userExtension).on(user.userExtension.extensionId.eq(userExtension.extensionId))
                .leftJoin(userAttachments).on(user.userId.eq(userAttachments.user.userId))
                .where(user.userId.eq(userId), user.isDeleted.eq(isDeleted), userAttachments.isDeleted.eq(isDeleted)).fetchOne();
    }

    public List<UserResponseDto> selectUserByCalendar(Long calendarId, Boolean isDeleted){
        QUser user = QUser.user;
        QCalendarUserRelation calendarUserRelation = QCalendarUserRelation.calendarUserRelation;
        return jpaQueryFactory
                .select(Projections.fields(UserResponseDto.class, user.userId, user.userName, user.nickName, user.email, user.phoneNumber, user.gender))
                .from(user)
                .leftJoin(calendarUserRelation).on(user.userId.eq(calendarUserRelation.user.userId))
                .where(calendarUserRelation.calendar.calendarId.eq(calendarId), calendarUserRelation.isDeleted.eq(isDeleted))
                .orderBy(calendarUserRelation.createDateTime.desc())
                .fetch();
    }

    public List<UserResponseDto> selectUserByCalendarContentRelation(Long contentId, Boolean isDeleted){
        QUser user = QUser.user;
        QCalendarContentUserRelation qcur = new QCalendarContentUserRelation("qcur");
        return jpaQueryFactory
                .select(Projections.fields(UserResponseDto.class,
                        user.userId, user.userName, user.nickName, user.email, user.phoneNumber, user.gender))
                .from(user)
                .leftJoin(qcur).on(user.userId.eq(qcur.user.userId))
                .where(qcur.calendarContent.contentId.eq(contentId), qcur.isDeleted.eq(isDeleted))
                .orderBy(qcur.relationId.desc())
                .fetch();
    }
}
