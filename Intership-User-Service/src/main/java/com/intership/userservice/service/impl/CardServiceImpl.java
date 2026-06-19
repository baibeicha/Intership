package com.intership.userservice.service.impl;

import com.intership.userservice.exception.CardNotFoundException;
import com.intership.userservice.mapper.CardDtoMapper;
import com.intership.userservice.model.dto.CardDto;
import com.intership.userservice.model.dto.UpdateCardDto;
import com.intership.userservice.model.entity.Card;
import com.intership.userservice.repository.CardRepository;
import com.intership.userservice.service.CardService;
import com.intership.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardDtoMapper cardMapper;
    private final UserService userService;

    @Transactional
    @Override
    @CacheEvict(value = "users", key = "#dto.userId")
    public CardDto createCard(CardDto dto) {
        Card card = cardMapper.toEntity(dto);
        card.setUser(userService.getById(dto.getUserId()));
        return save(card);
    }

    @Transactional
    @Override
    public CardDto save(Card card) {
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Override
    public CardDto findById(long id) {
        return cardMapper.toDto(cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card with id " + id + " not found"
                )));
    }

    @Override
    public List<CardDto> findAllByIds(List<Long> ids) {
        return cardMapper.toDtos(cardRepository.findAllByIdIn(ids));
    }

    @Override
    public List<CardDto> findAllByUserId(long userId) {
        return cardMapper.toDtos(cardRepository.findAllByUserId(userId));
    }

    @Transactional
    @Override
    @CacheEvict(value = "users", key = "#result.userId")
    public CardDto updateById(long id, UpdateCardDto cardDto) {
        cardRepository.updateById(id, cardDto.getNumber(), cardDto.getHolder(), cardDto.getExpirationDate());
        Card updatedCard = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with id " + id + " not found"));

        return cardMapper.toDto(updatedCard);
    }

    @Transactional
    @Override
    @CacheEvict(value = "users", key = "@cardRepository.findUserIdById(#id)", beforeInvocation = true)
    public void deleteById(long id) {
        cardRepository.deleteById(id);
    }
}