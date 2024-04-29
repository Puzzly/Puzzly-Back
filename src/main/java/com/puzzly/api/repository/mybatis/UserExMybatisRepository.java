package com.puzzly.api.repository.mybatis;

import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserEx;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserExMybatisRepository {
    public UserEx selectUserEx(@Param("userId") Long userId);
}
