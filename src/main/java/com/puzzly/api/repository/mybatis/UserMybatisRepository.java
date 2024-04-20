package com.puzzly.api.repository.mybatis;

import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMybatisRepository {

    public void insertUser(@Param("user") UserRequestDto userRequestDto);

}
