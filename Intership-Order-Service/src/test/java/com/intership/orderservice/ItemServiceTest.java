package com.intership.orderservice;

import com.intership.orderservice.exception.ItemNotFoundException;
import com.intership.orderservice.mapper.ItemMapper;
import com.intership.orderservice.model.dto.ItemRequest;
import com.intership.orderservice.model.dto.ItemResponse;
import com.intership.orderservice.model.entity.Item;
import com.intership.orderservice.repository.ItemRepository;
import com.intership.orderservice.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getById_whenExists_thenReturnItem() {
        Item item = new Item(1L, "name", BigDecimal.TEN, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item actual = itemService.getById(1L);

        assertThat(actual).isEqualTo(item);
        verify(itemRepository).findById(1L);
    }

    @Test
    void getById_whenNotExists_thenThrow() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found with id: 1");

        verify(itemRepository).findById(1L);
    }

    @Test
    void createItem_shouldSaveAndReturnDto() {
        ItemRequest request = new ItemRequest();
        request.setName("item");
        request.setPrice(BigDecimal.valueOf(5));

        Item toSave = new Item(null, "item", BigDecimal.valueOf(5), null);
        Item saved = new Item(10L, "item", BigDecimal.valueOf(5), null);
        ItemResponse response = new ItemResponse();
        response.setId(10L);
        response.setName("item");
        response.setPrice(BigDecimal.valueOf(5));

        when(itemMapper.toEntityFromRequest(request)).thenReturn(toSave);
        when(itemRepository.save(toSave)).thenReturn(saved);
        when(itemMapper.toDto(saved)).thenReturn(response);

        ItemResponse actual = itemService.createItem(request);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(10L);
        verify(itemMapper).toEntityFromRequest(request);
        verify(itemRepository).save(toSave);
    }

    @Test
    void updateItem_shouldUpdateAndReturnDto() {
        long id = 5L;
        ItemRequest request = new ItemRequest();
        request.setName("updated");
        request.setPrice(BigDecimal.valueOf(7));

        Item existing = new Item(id, "old", BigDecimal.TEN, null);
        Item saved = new Item(id, "updated", BigDecimal.valueOf(7), null);
        ItemResponse response = new ItemResponse();
        response.setId(id);
        response.setName("updated");
        response.setPrice(BigDecimal.valueOf(7));

        when(itemRepository.findById(id)).thenReturn(Optional.of(existing));
        doAnswer(invocation -> {
            Item target = invocation.getArgument(0);
            ItemRequest r = invocation.getArgument(1);
            target.setName(r.getName());
            target.setPrice(r.getPrice());
            return null;
        }).when(itemMapper).updateItemFromRequest(any(Item.class), eq(request));

        when(itemRepository.save(existing)).thenReturn(saved);
        when(itemMapper.toDto(saved)).thenReturn(response);

        ItemResponse actual = itemService.updateItem(id, request);

        assertThat(actual.getName()).isEqualTo("updated");
        assertThat(actual.getId()).isEqualTo(id);
        verify(itemMapper).updateItemFromRequest(existing, request);
        verify(itemRepository).save(existing);
    }

    @Test
    void deleteById_shouldCallRepository() {
        long id = 3L;
        doNothing().when(itemRepository).deleteById(id);

        itemService.deleteById(id);

        verify(itemRepository).deleteById(id);
    }

    @Test
    void findItemById_shouldReturnDto() {
        long id = 2L;
        Item item = new Item(id, "n", BigDecimal.ONE, null);
        ItemResponse dto = new ItemResponse();
        dto.setId(id);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(dto);

        ItemResponse actual = itemService.findItemById(id);

        assertThat(actual).isEqualTo(dto);
        verify(itemMapper).toDto(item);
    }
}
