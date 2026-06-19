package com.intership.orderservice;

import com.intership.orderservice.model.dto.OrderItemRequest;
import com.intership.orderservice.model.entity.Item;
import com.intership.orderservice.model.entity.OrderItem;
import com.intership.orderservice.service.ItemService;
import com.intership.orderservice.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    void toOrderItem_shouldReturnOrderItemWithItemAndQuantity() {
        OrderItemRequest request = new OrderItemRequest();
        request.setItemId(11L);
        request.setQuantity(3);

        Item item = new Item(11L, "it", BigDecimal.valueOf(2), null);
        when(itemService.getById(11L)).thenReturn(item);

        OrderItem orderItem = orderItemService.toOrderItem(request);

        assertThat(orderItem).isNotNull();
        assertThat(orderItem.getItem()).isEqualTo(item);
        assertThat(orderItem.getQuantity()).isEqualTo(3);
        verify(itemService).getById(11L);
    }
}
