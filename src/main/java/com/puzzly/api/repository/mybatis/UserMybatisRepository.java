package com.puzzly.api.repository.mybatis;

import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@Deprecated(forRemoval = true)
public interface UserMybatisRepository {
    /* MNIGRATED
    public UserResponseDto selectUser(@Param("userId") Long userId);
     */
    /* MIGRATED
    public User selectUserByEmail(@Param("email") String email);
     */
    /* MIGRATED
    public List<UserResponseDto> selectUserByCalendar(@Param("calendarId") Long calendarId);
     */
    /* MIGRATED
    public List<UserResponseDto> selectUserByCalendarContentRelation(@Param("contentId") Long contentId, @Param("isDeleted") Boolean isDeleted);
     */
    /* MIGRATED
    public List<String> selectUserAuthority(@Param("userId") Long userId);
     */
}
