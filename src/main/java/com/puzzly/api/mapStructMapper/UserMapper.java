package com.puzzly.api.mapStructMapper;

import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source="userExRequestDto", target="userEx")
    User toUser(UserRequestDto userRequestDto);
}
