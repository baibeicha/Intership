package com.intership.orderservice.mapper;

import com.intership.orderservice.model.dto.ItemRequest;
import com.intership.orderservice.model.dto.ItemResponse;
import com.intership.orderservice.model.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class)
public interface ItemMapper extends BaseMapper<Item, ItemResponse> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Item toEntityFromRequest(ItemRequest itemRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    void updateItemFromRequest(@MappingTarget Item item, ItemRequest itemRequestDTO);
}