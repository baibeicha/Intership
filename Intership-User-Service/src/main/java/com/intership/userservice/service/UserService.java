package com.intership.userservice.service;

import com.intership.userservice.model.dto.UpdateUserDto;
import com.intership.userservice.model.dto.UserDto;
import com.intership.userservice.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {

    @Transactional
    UserDto createUser(UserDto dto);

    @Transactional
    UserDto save(User user);

    @Transactional
    void deleteById(long id);

    UserDto findById(long id);

    User getById(long id);

    UserDto findByEmail(String email);

    List<UserDto> findAllByIds(List<Long> ids);

    @Transactional
    UserDto updateById(long id, UpdateUserDto userDto);
}
