package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

import static com.puzzly.api.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    /*
    ExpressionUtils.as(
        JPAExpressions.select(count(student.id))
                .from(student)
                .where(student.academy.eq(academy)),
        "studentCount")
     */

    /** 회원 이메일 존재 여부 조회*/
    public Boolean selectExistsEmailAndIsDeleted(String email, Boolean isDeleted){
        QUser user = QUser.user;

        return jpaQueryFactory
                .selectFrom(user)
                .where(user.email.eq(email), eqIsDeleted(isDeleted))
                .fetchFirst() != null;
    }
    @Deprecated
    public Boolean selectUserExistsByEmailAndIsDeleted(String email, Boolean isDeleted){
        QUser user = QUser.user;

        return jpaQueryFactory
                .selectFrom(user)
                .where(user.email.eq(email), user.isDeleted.eq(isDeleted))
                .fetchFirst() != null;
    }

    /** 회원 아이디 존재 여부 조회 */
    public Boolean selectExistsMemberIdAndIsDeleted(String email, Boolean isDeleted){
        QUser user = QUser.user;

        return jpaQueryFactory
                .selectFrom(user)
                .where(user.memberId.eq(email), eqIsDeleted(isDeleted))
                .fetchFirst() != null;
    }
    /** 사용자 PK로 정보조회 */
    public UserResponseDto selectUserByUserId(Long userId, Boolean isDeleted){
        QUser user = QUser.user;
        QUserExtension userExtension = new QUserExtension("userExtension");
        return jpaQueryFactory
                .select(Projections.fields(UserResponseDto.class,
                        user.userId, user.memberId, user.userName, user.nickName, user.phoneNumber,
                        user.birth, user.gender,user.email,
                        user.createDateTime,
                        userExtension.extensionId, userExtension.joinType, userExtension.statusMessage,
                        userExtension.profilePath, userExtension.extension, userExtension.originName,
                        userExtension.fileSize, userExtension.firstTermAgreement, userExtension.secondTermAgreement,
                        userExtension.personalSetting
                ))
                .from(user)
                .leftJoin(userExtension).on(user.userExtension.extensionId.eq(userExtension.extensionId))
                .where(user.userId.eq(userId), user.isDeleted.eq(isDeleted)).fetchOne();
    }

    @Deprecated
    public UserResponseDto selectUserByEmail(String email, Boolean isDeleted){
        QUser user = QUser.user;
        QUserExtension userExtension = new QUserExtension("userExtension");
        //QUserAttachments userAttachments = new QUserAttachments("userAttachments");
        return jpaQueryFactory
                .select(Projections.fields(UserResponseDto.class,
                        user.userId,user.userName, user.nickName,user.email, user.phoneNumber,
                        user.birth, user.gender,
                        // 스칼라 서브쿼리
                        //ExpressionUtils.as( JPAExpressions.select() )
                        //user.createDateTime,user.modifyDateTime,user.status,
                        userExtension.extensionId,userExtension.firstTermAgreement,
                        userExtension.secondTermAgreement,userExtension.statusMessage
                        ))
                .from(user)
                    .leftJoin(userExtension).on(user.userExtension.extensionId.eq(userExtension.extensionId))
                .where(user.email.eq(email), user.isDeleted.eq(isDeleted)).fetchOne();
    }

    public List<UserResponseDto> selectUserByCalendar(Long calendarId, Boolean isDeleted){
        QUser user = QUser.user;
        QCalendarUserRelation calendarUserRelation = QCalendarUserRelation.calendarUserRelation;
        return jpaQueryFactory
                .select(Projections.fields(UserResponseDto.class, user.userId, user.userName,
                        //user.nickName,
                                user.email, user.phoneNumber, user.gender))
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
                        user.userId, user.userName,
                        //user.nickName,
                                user.email, user.phoneNumber, user.gender))
                .from(user)
                .leftJoin(qcur).on(user.userId.eq(qcur.user.userId))
                .where(qcur.calendarContent.contentId.eq(contentId), qcur.isDeleted.eq(isDeleted))
                .orderBy(qcur.relationId.desc())
                .fetch();
    }

    private BooleanExpression eqIsDeleted(Boolean isDeleted){
        if(ObjectUtils.isEmpty(isDeleted)){
            return null;
        }
        return user.isDeleted.eq(isDeleted);
    }

}
