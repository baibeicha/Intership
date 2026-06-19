package com.intership.userservice.mapper;

import com.intership.userservice.model.dto.CardDto;
import com.intership.userservice.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = BaseMapper.class)
public abstract class CardDtoMapper implements BaseMapper<Card, CardDto> {

    @Mapping(target = "userId", source = "user.id")
    public abstract CardDto toDto(Card card);

    @Mapping(target = "user", ignore = true)
    public abstract Card toEntity(CardDto cardDto);

    public abstract List<CardDto> toDtos(List<Card> cards);

    public abstract List<Card> toEntities(List<CardDto> cardDtos);

    @Mapping(target = "user", ignore = true)
    public abstract Card merge(@MappingTarget Card card, CardDto cardDto);
}