package com.intership.userservice.service.impl;

import com.intership.userservice.exception.UserNotFoundException;
import com.intership.userservice.mapper.CardDtoMapper;
import com.intership.userservice.mapper.UserDtoMapper;
import com.intership.userservice.model.dto.CardDto;
import com.intership.userservice.model.dto.UpdateUserDto;
import com.intership.userservice.model.dto.UserDto;
import com.intership.userservice.model.entity.User;
import com.intership.userservice.repository.UserRepository;
import com.intership.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userMapper;
    private final CardDtoMapper cardMapper;

    @Transactional
    @Override
    public UserDto createUser(UserDto dto) {
        User user = userMapper.toEntity(dto);
        return save(user);
    }

    @Transactional
    @Override
    @CachePut(value = "users", key = "#result.id")
    public UserDto save(User user) {
        UserDto savedUser = userMapper.toDto(userRepository.save(user));
        List<CardDto> cards = cardMapper.toDtos(user.getCards());
        savedUser.setCards(cards);
        return savedUser;
    }

    @Transactional
    @Override
    @CacheEvict(value = "users", key = "#id")
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public UserDto findById(long id) {
        User user = getById(id);
        UserDto userDto = userMapper.toDto(user);
        List<CardDto> cards = cardMapper.toDtos(user.getCards());
        userDto.setCards(cards);
        return userDto;
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + id + " not found"
                ));
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "users", key = "#email", unless = "#result == null")
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with email " + email + " not found"
                ));
        UserDto userDto = userMapper.toDto(user);
        List<CardDto> cards = cardMapper.toDtos(user.getCards());
        userDto.setCards(cards);
        return userDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAllByIds(List<Long> ids) {
        return userMapper.toDtos(userRepository.findAllByIdIn(ids));
    }

    @Transactional
    @Override
    @CachePut(value = "users", key = "#id")
    public UserDto updateById(long id, UpdateUserDto userDto) {
        userRepository.updateById(id, userDto.getName(), userDto.getSurname(), userDto.getBirthDate());
        User updatedUser = getById(id);
        UserDto updatedUserDto = userMapper.toDto(updatedUser);
        List<CardDto> cards = cardMapper.toDtos(updatedUser.getCards());
        updatedUserDto.setCards(cards);
        return updatedUserDto;
    }
}