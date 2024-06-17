package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.UserResponseDto;

import java.util.List;

public interface UserJpaRepositoryCustom {
    public UserResponseDto selectUserByEmail(String email, Boolean isDeleted);

    public Boolean selectUserExistsByEmail(String email);

    public Boolean selectUserExistsByEmailAndIsDeleted(String email, Boolean isDeleted);

    public UserResponseDto selectUserByUserId(Long userId, Boolean isDeleted);

    public List<UserResponseDto> selectUserByCalendar(Long calendarId, Boolean isDeleted);

    public List<UserResponseDto> selectUserByCalendarContentRelation(Long contentId, Boolean isDeleted);
}
