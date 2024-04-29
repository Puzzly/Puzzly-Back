package com.puzzly.api.repository.mybatis;

import com.puzzly.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface UserMybatisRepository {
    public List<User> selectUser(@Param("userId") Long userId);

    public User selectUserByEmail(@Param("email") String email);
}
