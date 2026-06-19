package com.intership.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intership.orderservice.model.dto.OrderItemRequest;
import com.intership.orderservice.model.dto.OrderRequest;
import com.intership.orderservice.model.dto.OrderResponse;
import com.intership.orderservice.model.dto.UserResponse;
import com.intership.orderservice.model.entity.Item;
import com.intership.orderservice.model.entity.Order;
import com.intership.orderservice.repository.ItemRepository;
import com.intership.orderservice.repository.OrderRepository;
import com.intership.orderservice.service.OrderService;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { IntershipOrderServiceApplication.class, TestcontainersConfiguration.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceIntegrationTest {

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
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        WM.resetAll();
    }

    @Test
    void createOrder_whenUserServiceReturnsUser_thenOrderIsPersistedAndUserInfoAttached() throws Exception {
        Item item = new Item();
        item.setName("test-item");
        item.setPrice(BigDecimal.valueOf(15.75));
        item = itemRepository.save(item);

        UserResponse user = new UserResponse();
        user.setEmail("test-user@example.com");
        user.setName("Test");
        user.setSurname("Tester");

        WM.stubFor(get(urlPathEqualTo("/api/v1/user/email/" + user.getEmail()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(MAPPER.writeValueAsString(user))
                        .withStatus(200)));

        OrderItemRequest oi = new OrderItemRequest();
        oi.setItemId(item.getId());
        oi.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserEmail(user.getEmail());
        orderRequest.setStatus(Order.Status.PROCESSING);
        orderRequest.getOrderItems().add(oi);

        OrderResponse created = orderService.createOrder(orderRequest);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getUserInfo()).isNotNull();
        assertThat(created.getUserInfo().getEmail()).isEqualTo(user.getEmail());

        OrderResponse fetched = orderService.findOrderById(created.getId());
        assertThat(fetched).isNotNull();
        assertThat(fetched.getUserInfo()).isNotNull();
        assertThat(fetched.getUserInfo().getEmail()).isEqualTo(user.getEmail());
    }
}
