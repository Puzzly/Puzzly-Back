package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.UserResponseDto;

import java.util.List;

public interface UserRepositoryCustom {
    /** 회원 이메일 존재 여부 조회*/
    public Boolean selectExistsEmailAndIsDeleted(String email, Boolean isDeleted);
    /** 회원 아이디 중복 여부 조회*/
    public Boolean selectExistsMemberId(String memberId);
    /** 사용자 PK로 정보 조회*/
    public UserResponseDto selectUserByUserId(Long userId, Boolean isDeleted);
    @Deprecated
    public UserResponseDto selectUserByEmail(String email, Boolean isDeleted);



    public List<UserResponseDto> selectUserByCalendar(Long calendarId, Boolean isDeleted);

    public List<UserResponseDto> selectUserByCalendarContentRelation(Long contentId, Boolean isDeleted);
}
