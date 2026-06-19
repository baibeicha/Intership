package com.intership.authenticationservice.mapper;

import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.entity.UserCredentials;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = BaseMapper.class)
public abstract class UserCredentialsMapper implements BaseMapper<UserCredentials, AuthEntity> {
    public abstract AuthEntity toDto(UserCredentials entity);
    public abstract UserCredentials toEntity(AuthEntity dto);
    public abstract List<AuthEntity> toDtos(List<UserCredentials> entities);
    public abstract List<UserCredentials> toEntities(List<AuthEntity> dtos);
    public abstract UserCredentials merge(@MappingTarget UserCredentials entity, AuthEntity dto);
}
