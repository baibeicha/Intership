package com.intership.orderservice;

import com.intership.orderservice.exception.UserNotFoundException;
import com.intership.orderservice.exception.UserServiceException;
import com.intership.orderservice.model.dto.UserResponse;
import com.intership.orderservice.service.impl.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceClientUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceClient userServiceClient;

    @Test
    void getUserByEmail_whenSuccess_thenReturnUser() {
        String email = "a@b";
        UserResponse response = new UserResponse();
        response.setEmail(email);

        when(restTemplate.getForObject(anyString(), eq(UserResponse.class))).thenReturn(response);

        UserResponse actual = userServiceClient.getUserByEmail(email);

        assertThat(actual).isEqualTo(response);
        verify(restTemplate).getForObject(contains("/email/" + email), eq(UserResponse.class));
    }

    @Test
    void getUserByEmail_whenNotFound_thenThrowUserNotFoundException() {
        String email = "no@one";
        when(restTemplate.getForObject(anyString(), eq(UserResponse.class)))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        HttpStatusCode.valueOf(404), "Not found",
                        null, null, null)
                );

        assertThatThrownBy(() -> userServiceClient.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email);
    }

    @Test
    void getUserByEmail_whenOtherError_thenThrowUserServiceException() {
        String email = "err@ex";
        when(restTemplate.getForObject(anyString(), eq(UserResponse.class)))
                .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> userServiceClient.getUserByEmail(email))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("Failed to fetch user information for user with email: " + email);
    }
}

