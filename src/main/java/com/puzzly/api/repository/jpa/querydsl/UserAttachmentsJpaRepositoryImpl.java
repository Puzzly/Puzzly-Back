package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.UserAttachmentsResponseDto;
//import com.puzzly.api.entity.QUserAttachments;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class UserAttachmentsJpaRepositoryImpl {
/*
    private final JPAQueryFactory jpaQueryFactory;

    public UserAttachmentsResponseDto selectUserAttachmentsByUserId(Long userId, Boolean isDeleted){
        QUserAttachments userAttachments = QUserAttachments.userAttachments;
        return jpaQueryFactory
                .select(Projections.fields(UserAttachmentsResponseDto.class,
                        userAttachments.attachmentsId, userAttachments.extension,
                        userAttachments.originName, userAttachments.filePath,
                        userAttachments.fileSize, userAttachments.createDateTime))
                .from(userAttachments)
                .where(userAttachments.user.userId.eq(userId), userAttachments.isDeleted.eq(isDeleted))
                .fetchOne();
    }

 */
}
