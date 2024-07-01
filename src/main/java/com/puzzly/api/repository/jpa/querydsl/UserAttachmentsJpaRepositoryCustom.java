package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.UserAttachmentsResponseDto;

@Deprecated(forRemoval = true)
public interface UserAttachmentsJpaRepositoryCustom {
    public UserAttachmentsResponseDto selectUserAttachmentsByUserId(Long userId, Boolean isDeleted);
}
