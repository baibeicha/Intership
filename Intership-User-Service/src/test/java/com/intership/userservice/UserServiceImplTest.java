package com.intership.userservice;

import com.intership.userservice.exception.UserNotFoundException;
import com.intership.userservice.mapper.CardDtoMapper;
import com.intership.userservice.mapper.UserDtoMapper;
import com.intership.userservice.model.dto.UpdateUserDto;
import com.intership.userservice.model.dto.UserDto;
import com.intership.userservice.model.entity.User;
import com.intership.userservice.repository.UserRepository;
import com.intership.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoMapper userMapper;

    @Mock
    private CardDtoMapper cardMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        userDto = new UserDto();
        userDto.setId(null);
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setBirthDate(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createUser_shouldSaveAndReturnDto() {
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);
        when(cardMapper.toDtos(any())).thenReturn(List.of());

        UserDto result = userService.createUser(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getById_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(42L));
    }

    @Test
    void findById_shouldReturnUserDtoWithCards() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(cardMapper.toDtos(user.getCards())).thenReturn(List.of());

        UserDto result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getCards()).isEmpty();
    }

    @Test
    void findByEmail_whenNotFound_shouldThrow() {
        when(userRepository.findByEmail("no@mail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByEmail("no@mail.com"));
    }

    @ParameterizedTest
    @MethodSource("provideIdLists")
    void findAllByIds_shouldReturnList_forVariousInputs(List<Long> ids) {
        List<User> repoResult = ids.isEmpty() ? List.of() : List.of(user);
        List<UserDto> mapperResult = ids.isEmpty() ? List.of() : List.of(userDto);

        when(userRepository.findAllByIdIn(ids)).thenReturn(repoResult);
        when(userMapper.toDtos(repoResult)).thenReturn(mapperResult);

        List<UserDto> result = userService.findAllByIds(ids);

        if (ids.isEmpty()) {
            assertThat(result).isEmpty();
        } else {
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getEmail()).isEqualTo(user.getEmail());
        }
    }

    static Stream<List<Long>> provideIdLists() {
        return Stream.of(
                List.of(1L),
                List.of(1L, 2L),
                List.of()
        );
    }

    @Test
    void updateById_shouldCallUpdateAndReturnUpdatedDto() {
        UpdateUserDto update = new UpdateUserDto();
        update.setName("Jane");
        update.setSurname("Roe");
        update.setBirthDate(LocalDate.of(1991, 2, 2));

        User updated = new User();
        updated.setId(1L);
        updated.setName("Jane");
        updated.setSurname("Roe");
        updated.setEmail(user.getEmail());
        updated.setBirthDate(update.getBirthDate());

        doNothing().when(userRepository).updateById(
                eq(1L), eq(update.getName()), eq(update.getSurname()), eq(update.getBirthDate()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(updated));
        when(userMapper.toDto(updated)).thenReturn(userDto);
        when(cardMapper.toDtos(updated.getCards())).thenReturn(List.of());

        UserDto result = userService.updateById(1L, update);

        assertThat(result).isNotNull();
        verify(userRepository).updateById(
                eq(1L), eq(update.getName()), eq(update.getSurname()), eq(update.getBirthDate()));
    }

    @Test
    void updateById_whenUserMissingAfterUpdate_shouldThrowUserNotFound() {
        UpdateUserDto update = new UpdateUserDto();
        update.setName("Ghost");
        update.setSurname("User");
        update.setBirthDate(LocalDate.of(2000, 1, 1));

        doNothing().when(userRepository).updateById(
                eq(1L), eq(update.getName()), eq(update.getSurname()), eq(update.getBirthDate()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateById(1L, update));

        verify(userRepository).updateById(
                eq(1L), eq(update.getName()), eq(update.getSurname()), eq(update.getBirthDate()));
    }

    @Test
    void deleteById_shouldCallRepository() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }
}
