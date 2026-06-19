package com.intership.userservice.service;

import com.intership.userservice.model.dto.CardDto;
import com.intership.userservice.model.dto.UpdateCardDto;
import com.intership.userservice.model.entity.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardService {

    @Transactional
    CardDto createCard(CardDto dto);

    @Transactional
    CardDto save(Card card);

    CardDto findById(long id);

    @Transactional
    void deleteById(long id);

    List<CardDto> findAllByIds(List<Long> ids);

    List<CardDto> findAllByUserId(long userId);

    @Transactional
    CardDto updateById(long id, UpdateCardDto cardDto);
}
