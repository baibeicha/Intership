package com.intership.authenticationservice;

import com.intership.authenticationservice.exception.UserCredentialsNotFoundException;
import com.intership.authenticationservice.exception.UserRegistrationException;
import com.intership.authenticationservice.mapper.UserCredentialsMapper;
import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.UserDto;
import com.intership.authenticationservice.model.dto.UserRegistrationRequest;
import com.intership.authenticationservice.model.entity.UserCredentials;
import com.intership.authenticationservice.repository.UserCredentialsRepository;
import com.intership.authenticationservice.service.UserService;
import com.intership.authenticationservice.service.impl.UserCredentialsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCredentialsServiceImplTest {

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserCredentialsMapper userCredentialsMapper;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<UserCredentials> userCredentialsCaptor;

    private UserCredentialsServiceImpl userCredentialsService;

    private final String USERNAME = "testuser";
    private final String PASSWORD = "password";
    private final String ENCODED_PASSWORD = "encodedPassword";
    private final Long USER_ID = 1L;
    private final String EMAIL = "test@example.com";
    private final String NAME = "Test";
    private final String SURNAME = "User";
    private final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 1);

    @BeforeEach
    void setUp() {
        userCredentialsService = new UserCredentialsServiceImpl(
                userCredentialsRepository, passwordEncoder, userCredentialsMapper, userService
        );
    }

    @Test
    void existsByUsername_WhenUserExists_ShouldReturnTrue() {
        when(userCredentialsRepository.existsByUsername(USERNAME)).thenReturn(true);

        boolean result = userCredentialsService.existsByUsername(USERNAME);

        assertTrue(result);
        verify(userCredentialsRepository).existsByUsername(USERNAME);
    }

    @Test
    void existsByUsername_WhenUserNotExists_ShouldReturnFalse() {
        when(userCredentialsRepository.existsByUsername(USERNAME)).thenReturn(false);

        boolean result = userCredentialsService.existsByUsername(USERNAME);

        assertFalse(result);
        verify(userCredentialsRepository).existsByUsername(USERNAME);
    }

    @Test
    void getByUsername_WhenUserExists_ShouldReturnUserCredentials() {
        UserCredentials userCredentials = new UserCredentials(1L, USER_ID, USERNAME, PASSWORD);
        when(userCredentialsRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(userCredentials));

        UserCredentials result = userCredentialsService.getByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(USER_ID, result.getUserId());
        verify(userCredentialsRepository).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_WhenUserNotExists_ShouldThrowException() {
        when(userCredentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserCredentialsNotFoundException.class,
                () -> userCredentialsService.getByUsername(USERNAME));
        verify(userCredentialsRepository).findByUsername(USERNAME);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnAuthEntity() {
        UserCredentials userCredentials = new UserCredentials(1L, USER_ID, USERNAME, PASSWORD);
        AuthEntity authEntity = new AuthEntity(USERNAME, PASSWORD);

        when(userCredentialsRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(userCredentials));
        when(userCredentialsMapper.toDto(userCredentials)).thenReturn(authEntity);

        AuthEntity result = userCredentialsService.findByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        verify(userCredentialsRepository).findByUsername(USERNAME);
        verify(userCredentialsMapper).toDto(userCredentials);
    }

    @Test
    void register_WhenNewUser_ShouldCreateUserAndReturnUserDto() {
        UserRegistrationRequest request = createUserRegistrationRequest();
        AuthEntity authEntityForSave = new AuthEntity(USERNAME, PASSWORD);
        AuthEntity encodedAuth = new AuthEntity(USERNAME, ENCODED_PASSWORD);
        UserDto expectedUserDto = createUserDto();

        UserCredentials initialUserCredentials = new UserCredentials(null, null, USERNAME, ENCODED_PASSWORD);
        UserCredentials savedUserCredentials = new UserCredentials(1L, null, USERNAME, ENCODED_PASSWORD);

        // Mock the save method behavior
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userCredentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(userCredentialsMapper.toEntity(encodedAuth)).thenReturn(initialUserCredentials);
        when(userCredentialsRepository.save(any(UserCredentials.class))).thenReturn(savedUserCredentials);
        when(userCredentialsMapper.toDto(any(UserCredentials.class))).thenReturn(encodedAuth);

        // Mock user service call
        when(userService.registerUser(request)).thenReturn(expectedUserDto);

        UserDto result = userCredentialsService.register(request);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(EMAIL, result.getEmail());

        // Verify interactions
        verify(passwordEncoder).encode(PASSWORD);
        verify(userCredentialsRepository, times(2)).save(userCredentialsCaptor.capture());
        verify(userService).registerUser(request);

        // Verify that userId was set on the second save call
        UserCredentials secondSaveCall = userCredentialsCaptor.getAllValues().get(1);
        assertEquals(USER_ID, secondSaveCall.getUserId());
    }

    @Test
    void register_WhenUserServiceThrowsException_ShouldDeleteCredentials() {
        UserRegistrationRequest request = createUserRegistrationRequest();
        AuthEntity authEntityForSave = new AuthEntity(USERNAME, PASSWORD);
        AuthEntity encodedAuth = new AuthEntity(USERNAME, ENCODED_PASSWORD);

        UserCredentials initialUserCredentials = new UserCredentials(null, null, USERNAME, ENCODED_PASSWORD);
        UserCredentials savedUserCredentials = new UserCredentials(1L, null, USERNAME, ENCODED_PASSWORD);

        // Mock the save method behavior
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userCredentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(userCredentialsMapper.toEntity(encodedAuth)).thenReturn(initialUserCredentials);
        when(userCredentialsRepository.save(any(UserCredentials.class))).thenReturn(savedUserCredentials);
        when(userCredentialsMapper.toDto(any(UserCredentials.class))).thenReturn(encodedAuth);

        // Mock user service to throw exception
        when(userService.registerUser(request)).thenThrow(new UserRegistrationException("Service error"));

        assertThrows(UserRegistrationException.class,
                () -> userCredentialsService.register(request));

        // Verify that delete was called when registration failed
        verify(userCredentialsRepository).deleteByUsername(USERNAME);
    }

    @Test
    void save_WhenNewUser_ShouldSaveAndReturnAuthEntity() {
        AuthEntity inputAuth = new AuthEntity(USERNAME, PASSWORD);
        AuthEntity encodedAuth = new AuthEntity(USERNAME, ENCODED_PASSWORD);
        UserCredentials userCredentials = new UserCredentials(null, null, USERNAME, ENCODED_PASSWORD);
        UserCredentials savedUserCredentials = new UserCredentials(1L, USER_ID, USERNAME, ENCODED_PASSWORD);

        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userCredentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(userCredentialsMapper.toEntity(encodedAuth)).thenReturn(userCredentials);
        when(userCredentialsRepository.save(userCredentials)).thenReturn(savedUserCredentials);
        when(userCredentialsMapper.toDto(savedUserCredentials)).thenReturn(encodedAuth);

        AuthEntity result = userCredentialsService.save(inputAuth);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(ENCODED_PASSWORD, result.getPassword());
        verify(passwordEncoder).encode(PASSWORD);
        verify(userCredentialsRepository).findByUsername(USERNAME);
        verify(userCredentialsRepository).save(userCredentials);
    }

    @Test
    void save_WhenExistingUser_ShouldUpdateAndReturnAuthEntity() {
        AuthEntity inputAuth = new AuthEntity(USERNAME, PASSWORD);
        AuthEntity encodedAuth = new AuthEntity(USERNAME, ENCODED_PASSWORD);
        UserCredentials existingUser = new UserCredentials(1L, USER_ID, USERNAME, "oldPassword");
        UserCredentials mergedUser = new UserCredentials(1L, USER_ID, USERNAME, ENCODED_PASSWORD);

        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userCredentialsRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(existingUser));
        when(userCredentialsMapper.merge(existingUser, encodedAuth)).thenReturn(mergedUser);
        when(userCredentialsRepository.save(mergedUser)).thenReturn(mergedUser);
        when(userCredentialsMapper.toDto(mergedUser)).thenReturn(encodedAuth);

        AuthEntity result = userCredentialsService.save(inputAuth);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        verify(passwordEncoder).encode(PASSWORD);
        verify(userCredentialsMapper).merge(existingUser, encodedAuth);
        verify(userCredentialsRepository).save(mergedUser);
    }

    @Test
    void deleteByUsername_ShouldCallRepository() {
        userCredentialsService.deleteByUsername(USERNAME);

        verify(userCredentialsRepository).deleteByUsername(USERNAME);
    }

    private UserRegistrationRequest createUserRegistrationRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        request.setName(NAME);
        request.setSurname(SURNAME);
        request.setBirthDate(BIRTH_DATE);
        request.setEmail(EMAIL);
        return request;
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setName(NAME);
        userDto.setSurname(SURNAME);
        userDto.setBirthDate(BIRTH_DATE);
        userDto.setEmail(EMAIL);
        return userDto;
    }
}