package com.intership.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intership.orderservice.exception.UserNotFoundException;
import com.intership.orderservice.model.dto.UserResponse;
import com.intership.orderservice.service.impl.UserServiceClient;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = { IntershipOrderServiceApplication.class, TestcontainersConfiguration.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceClientIntegrationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int WIREMOCK_PORT = 9571;

    @RegisterExtension
    static final WireMockExtension WM = WireMockExtension.newInstance()
            .options(wireMockConfig().port(WIREMOCK_PORT))
            .build();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("service.user", () -> "http://localhost:" + WIREMOCK_PORT);
    }

    @Autowired
    private UserServiceClient userServiceClient;

    @Test
    void getUserByEmail_whenUserExists_returnsUserResponse() throws Exception {
        UserResponse mockUser = new UserResponse();
        mockUser.setEmail("test@email.com");
        mockUser.setName("Test");
        mockUser.setSurname("Test");

        WM.stubFor(get(urlPathEqualTo("/api/v1/user/email/" + mockUser.getEmail()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(MAPPER.writeValueAsString(mockUser))
                        .withStatus(200)));

        UserResponse actual = userServiceClient.getUserByEmail(mockUser.getEmail());

        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(mockUser.getEmail());
        assertThat(actual.getName()).isEqualTo(mockUser.getName());
    }

    @Test
    void getUserByEmail_whenNotFound_throwsUserNotFoundException() {
        String email = "test@email.com";

        WM.stubFor(get(urlPathEqualTo("/api/v1/user/email/" + email))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> userServiceClient.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email);
    }
}
