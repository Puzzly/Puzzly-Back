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

    public List<UserResponseDto> selectUserByCalendarId(@Param("calendarId") Long calendarId);


    public List<String> selectUserAuthority(@Param("userId") Long userId);
}
