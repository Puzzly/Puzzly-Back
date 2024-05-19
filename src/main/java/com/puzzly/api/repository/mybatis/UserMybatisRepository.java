package com.puzzly.api.repository.mybatis;

import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMybatisRepository {
    public UserResponseDto selectUser(@Param("userId") Long userId);

    public User selectUserByEmail(@Param("email") String email);

    public List<UserResponseDto> selectUserByCalendar(@Param("calendarId") Long calendarId);

    public List<UserResponseDto> selectUserByCalendarContentRelation(@Param("contentId") Long contentId, @Param("isDeleted") Boolean isDeleted);

    public List<String> selectUserAuthority(@Param("userId") Long userId);

}
