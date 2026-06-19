package com.intership.paymentservice;

import com.intership.paymentservice.service.RandomNumberService;
import com.intership.paymentservice.service.impl.RandomNumberApiService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

class RandomNumberApiServiceUnitTest {

    private WireMockServer wireMockServer;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        restTemplate = new RestTemplate();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getRandomNumber_returnsParsedInt() {
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/random"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody("42")
                ));

        RandomNumberService svc = new RandomNumberApiService(restTemplate, "http://localhost:" + wireMockServer.port() + "/random");

        int result = svc.getRandomNumber();

        assertThat(result).isEqualTo(42);
    }

    @Test
    void getRandomNumber_onError_returns1() {
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/random"))
                .willReturn(WireMock.aResponse()
                        .withStatus(500)
                ));

        RandomNumberService svc = new RandomNumberApiService(restTemplate, "http://localhost:" + wireMockServer.port() + "/random");

        int result = svc.getRandomNumber();

        assertThat(result).isEqualTo(1);
    }
}
