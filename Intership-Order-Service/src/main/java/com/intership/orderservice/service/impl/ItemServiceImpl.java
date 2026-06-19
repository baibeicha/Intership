package com.intership.orderservice.service.impl;

import com.intership.orderservice.exception.ItemNotFoundException;
import com.intership.orderservice.mapper.ItemMapper;
import com.intership.orderservice.model.dto.ItemRequest;
import com.intership.orderservice.model.dto.ItemResponse;
import com.intership.orderservice.model.entity.Item;
import com.intership.orderservice.repository.ItemRepository;
import com.intership.orderservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public Item getById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    @Transactional
    @Override
    public ItemResponse createItem(ItemRequest request) {
        Item item = itemMapper.toEntityFromRequest(request);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemResponse updateItem(long id, ItemRequest request) {
        Item item = getById(id);
        itemMapper.updateItemFromRequest(item, request);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemResponse findItemById(long id) {
        return itemMapper.toDto(getById(id));
    }
}
