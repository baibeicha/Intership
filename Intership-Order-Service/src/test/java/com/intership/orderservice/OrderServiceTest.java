package com.intership.orderservice;

import com.intership.orderservice.exception.OrderNotFoundException;
import com.intership.orderservice.mapper.OrderMapper;
import com.intership.orderservice.model.dto.OrderItemRequest;
import com.intership.orderservice.model.dto.OrderRequest;
import com.intership.orderservice.model.dto.OrderResponse;
import com.intership.orderservice.model.dto.UserResponse;
import com.intership.orderservice.model.entity.Order;
import com.intership.orderservice.model.entity.OrderItem;
import com.intership.orderservice.repository.OrderRepository;
import com.intership.orderservice.service.impl.OrderItemServiceImpl;
import com.intership.orderservice.service.impl.OrderServiceImpl;
import com.intership.orderservice.service.impl.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemServiceImpl orderItemService;

    @Mock
    private UserServiceClient userService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest request;
    private OrderItemRequest oiReq;

    @BeforeEach
    void setUp() {
        oiReq = new OrderItemRequest();
        oiReq.setItemId(1L);
        oiReq.setQuantity(2);

        request = new OrderRequest();
        request.setUserEmail("u@example.com");
        request.setStatus(Order.Status.PROCESSING);
        request.getOrderItems().add(oiReq);
    }

    @Test
    void createOrder_shouldSaveAndReturnWithUserInfo() {
        Order orderEntity = new Order(null, request.getUserEmail(), request.getStatus(), LocalDateTime.now(), null);
        Order saved = new Order(7L, request.getUserEmail(), request.getStatus(), LocalDateTime.now(), null);
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(2);

        OrderResponse dto = new OrderResponse();
        dto.setId(7L);
        dto.setUserEmail(request.getUserEmail());

        UserResponse user = new UserResponse();
        user.setEmail(request.getUserEmail());

        when(orderMapper.toEntityFromRequest(request)).thenReturn(orderEntity);
        when(orderItemService.toOrderItem(oiReq)).thenReturn(orderItem);
        when(orderRepository.save(orderEntity)).thenReturn(saved);
        when(orderMapper.toDto(saved)).thenReturn(dto);
        when(userService.getUserByEmail(request.getUserEmail())).thenReturn(user);

        OrderResponse actual = orderService.createOrder(request);

        assertThat(actual.getId()).isEqualTo(7L);
        assertThat(actual.getUserInfo()).isEqualTo(user);

        verify(orderItemService).toOrderItem(oiReq);
        verify(orderRepository).save(orderEntity);
        verify(userService).getUserByEmail(request.getUserEmail());
    }

    @Test
    void findOrderById_whenExists_shouldReturnWithUserInfo() {
        long id = 5L;
        Order order = new Order(id, "a@b", Order.Status.CONFIRMED, LocalDateTime.now(), null);
        OrderResponse dto = new OrderResponse();
        dto.setId(id);
        dto.setUserEmail("a@b");
        UserResponse user = new UserResponse();
        user.setEmail("a@b");

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);
        when(userService.getUserByEmail("a@b")).thenReturn(user);

        OrderResponse actual = orderService.findOrderById(id);

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getUserInfo()).isEqualTo(user);
    }

    @Test
    void findOrderById_whenNotExists_shouldThrow() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrderById(100L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: 100");
    }

    @Test
    void findOrdersByIds_shouldReturnWithUserInfo() {
        Order order1 = new Order(1L, "u1", Order.Status.PROCESSING, LocalDateTime.now(), null);
        Order order2 = new Order(2L, "u2", Order.Status.PROCESSING, LocalDateTime.now(), null);

        OrderResponse r1 = new OrderResponse(); r1.setId(1L); r1.setUserEmail("u1");
        OrderResponse r2 = new OrderResponse(); r2.setId(2L); r2.setUserEmail("u2");

        UserResponse u1 = new UserResponse(); u1.setEmail("u1");
        UserResponse u2 = new UserResponse(); u2.setEmail("u2");

        when(orderRepository.findByIdIn(List.of(1L,2L))).thenReturn(List.of(order1, order2));
        when(orderMapper.toDto(order1)).thenReturn(r1);
        when(orderMapper.toDto(order2)).thenReturn(r2);
        when(userService.getUsersByEmails(List.of("u1","u2"))).thenReturn(Map.of("u1", u1, "u2", u2));

        var actual = orderService.findOrdersByIds(List.of(1L,2L));

        assertThat(actual).hasSize(2);
        assertThat(actual).extracting("userInfo").contains(u1, u2);
    }

    @Test
    void findOrdersByStatuses_shouldReturnWithUserInfo() {
        Order o1 = new Order(1L, "aa", Order.Status.CONFIRMED, LocalDateTime.now(), null);
        Order o2 = new Order(2L, "bb", Order.Status.CONFIRMED, LocalDateTime.now(), null);

        OrderResponse r1 = new OrderResponse(); r1.setId(1L); r1.setUserEmail("aa");
        OrderResponse r2 = new OrderResponse(); r2.setId(2L); r2.setUserEmail("bb");

        UserResponse u1 = new UserResponse(); u1.setEmail("aa");
        UserResponse u2 = new UserResponse(); u2.setEmail("bb");

        when(orderRepository.findByStatusIn(List.of(Order.Status.CONFIRMED))).thenReturn(List.of(o1, o2));
        when(orderMapper.toDto(o1)).thenReturn(r1);
        when(orderMapper.toDto(o2)).thenReturn(r2);
        when(userService.getUsersByEmails(List.of("aa","bb"))).thenReturn(Map.of("aa", u1, "bb", u2));

        var actual = orderService.findOrdersByStatuses(List.of(Order.Status.CONFIRMED));

        assertThat(actual).hasSize(2);
        assertThat(actual).extracting("userInfo").contains(u1, u2);
    }

    @ParameterizedTest
    @CsvSource({
            "1, true",
            "0, false"
    })
    void updateOrder_parameterized(int updatedRows, boolean shouldSucceed) {
        long id = 9L;
        OrderRequest req = new OrderRequest();
        req.setUserEmail("x@y");
        req.setStatus(Order.Status.SHIPPED);

        when(orderRepository.updateById(id, req.getUserEmail(), req.getStatus())).thenReturn(updatedRows);

        if (shouldSucceed) {
            OrderResponse resp = new OrderResponse();
            resp.setId(id);
            resp.setUserEmail(req.getUserEmail());
            when(orderRepository.findById(id)).thenReturn(Optional.of(new Order(id, req.getUserEmail(), req.getStatus(), LocalDateTime.now(), null)));
            when(orderMapper.toDto(any(Order.class))).thenReturn(resp);
            when(userService.getUserByEmail(req.getUserEmail())).thenReturn(new UserResponse());

            var actual = orderService.updateOrder(id, req);
            assertThat(actual.getId()).isEqualTo(id);
        } else {
            assertThatThrownBy(() -> orderService.updateOrder(id, req))
                    .isInstanceOf(OrderNotFoundException.class)
                    .hasMessageContaining("Order not found with id: " + id);
        }

        verify(orderRepository).updateById(id, req.getUserEmail(), req.getStatus());
    }

    @Test
    void deleteOrder_shouldCallRepository() {
        long id = 12L;
        doNothing().when(orderRepository).deleteById(id);

        orderService.deleteOrder(id);

        verify(orderRepository).deleteById(id);
    }
}
