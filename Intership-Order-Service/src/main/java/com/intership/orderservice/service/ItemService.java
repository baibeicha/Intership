package com.intership.orderservice.service;

import com.intership.orderservice.model.dto.ItemRequest;
import com.intership.orderservice.model.dto.ItemResponse;
import com.intership.orderservice.model.entity.Item;

public interface ItemService {
    Item getById(long id);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(long id, ItemRequest request);
    void deleteById(long id);
    ItemResponse findItemById(long id);
}
