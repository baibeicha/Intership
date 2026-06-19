package com.intership.userservice.mapper;

import com.intership.userservice.model.dto.UserDto;
import com.intership.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = BaseMapper.class)
public abstract class UserDtoMapper implements BaseMapper<User, UserDto> {

    @Mapping(target = "cards", ignore = true)
    public abstract UserDto toDto(User user);

    public abstract User toEntity(UserDto userDto);

    public abstract List<UserDto> toDtos(List<User> users);

    public abstract List<User> toEntities(List<UserDto> userDtos);

    public abstract User merge(@MappingTarget User user, UserDto userDto);
}
